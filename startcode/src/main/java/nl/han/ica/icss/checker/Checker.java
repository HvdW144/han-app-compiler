package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;


public class Checker {
    private static final String[] PROPERTIES = new String[]{"width", "height", "color", "background-color"};

    // teacher said we could use a normal linked list for this, so I changed it
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;
    private final ExpressionTypeHelper expressionTypeHelper = new ExpressionTypeHelper();
    private final IfCheckHelper ifCheckHelper = new IfCheckHelper();

    public void check(AST ast) {
        // clean up the variableTypes list
        variableTypes = new LinkedList<>();

        //start checking from the root node
        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        if (node instanceof Stylesheet) {
            checkStylesheet((Stylesheet) node);
        } else if (node instanceof Stylerule) {
            checkStylerule((Stylerule) node);
        } else if (node instanceof VariableAssignment) {
            checkVariableAssignment((VariableAssignment) node);
        } else if (node instanceof VariableReference) {
            checkVariableReference((VariableReference) node);
        } else if (node instanceof Declaration) {
            checkDeclaration((Declaration) node);
        } else if (node instanceof PropertyName) {
            checkPropertyName((PropertyName) node);
        } else if (node instanceof Operation) {
            checkOperation((Operation) node);
        } else if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
        } else if (node instanceof ElseClause) {
            checkElseClause((ElseClause) node);
        } else if (node instanceof Selector) {
            checkSelectorNode((Selector) node);
        }
    }

    /**
     * Check all child nodes of the given node.
     *
     * @param node The node to check the children of.
     */
    private void checkChildNodes(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
    }

    private void checkStylesheet(Stylesheet node) {
        //add scope
        variableTypes.add(new HashMap<>());

        checkChildNodes(node);

        //remove scope
        variableTypes.removeLast();
    }

    private void checkStylerule(Stylerule node) {
        //add scope
        variableTypes.add(new HashMap<>());

        checkChildNodes(node);

        //remove scope
        variableTypes.removeLast();
    }

    //--------------Variables--------------
    private void checkVariableAssignment(VariableAssignment node) {
        //check if variable is already declared in scope
        if (variableTypes.getLast().containsKey(node.name.name)) {
            node.setError("Variable " + node.name.name + " already declared within scope");
        }

        //otherwise add the variable to the current scope
        variableTypes.getLast().put(node.name.name, expressionTypeHelper.getVariableType(node.expression, variableTypes));
        checkChildNodes(node);
    }

    private void checkVariableReference(VariableReference node) {
        if (variableTypes.stream().noneMatch(scope -> scope.containsKey(node.name))) {
            node.setError("Variable " + node.name + " not declared");
        }
    }

    //--------------Declarations--------------
    private void checkDeclaration(Declaration node) {
        checkChildNodes(node);
    }

    private void checkPropertyName(PropertyName node) {
        if (Arrays.stream(PROPERTIES).noneMatch(property -> property.equals(node.name))) {
            node.setError("Unknown property " + node.name);
        }
    }

    //--------------Selectors--------------
    private void checkSelectorNode(Selector node) {
        //skip all selectors
    }

    //--------------Expressions--------------
    private void checkOperation(Operation node) {
        checkChildNodes(node);

        ExpressionType leftType = expressionTypeHelper.getVariableType(node.lhs, variableTypes);
        ExpressionType rightType = expressionTypeHelper.getVariableType(node.rhs, variableTypes);

        //check if no colors are used in operations
        if (leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) {
            node.setError("Operations with colors are not allowed");
            return;
        }

        //check if no booleans are used in operations
        if (leftType == ExpressionType.BOOL || rightType == ExpressionType.BOOL) {
            node.setError("Operations with booleans are not allowed");
            return;
        }

        //check if types are the same
        if (!(node instanceof MultiplyOperation)) {
            if (leftType != rightType) {
                node.setError("Operation between different types");
            }
            //check if multiply operations contain at least one scalar
        } else if (leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR) {
            node.setError("Multiply operations should contain at least one scalar");
        }
    }

    //--------------IF support--------------
    private void checkIfClause(IfClause node) {
        //add scope
        variableTypes.add(new HashMap<>());

        if (!(node.conditionalExpression instanceof BoolLiteral)) {
            if (node.conditionalExpression instanceof VariableReference) {
                ifCheckHelper.checkVariableReferenceInScopes(node, variableTypes);
            } else {
                node.setError("Conditional expression is not a boolean");
            }
        }
        checkChildNodes(node);

        //remove scope
        variableTypes.removeLast();
    }

    private void checkElseClause(ElseClause node) {
        //add scope
        variableTypes.add(new HashMap<>());

        checkChildNodes(node);

        //remove scope
        variableTypes.removeLast();
    }
}
