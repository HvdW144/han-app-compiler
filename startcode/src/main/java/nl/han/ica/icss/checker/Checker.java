package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    // teacher said we could use a normal linked list for this, so I changed it
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        // clean up the variableTypes list
        variableTypes = new LinkedList<>();

        //start checking from the root node
        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        // TODO: make generic?
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
        } else if (node instanceof Operation) {
            checkOperation((Operation) node);
        } else if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
        } else if (node instanceof Selector) {
            checkSelectorNode((Selector) node);
        } else {
            System.out.println("Unknown node type: " + node.getClass().getName());
        }
    }

    private void checkSelectorNode(Selector node) {
        if (node instanceof TagSelector) {
            checkTagSelector((TagSelector) node);
        } else if (node instanceof ClassSelector) {
            checkClassSelector((ClassSelector) node);
        } else if (node instanceof IdSelector) {
            checkIdSelector((IdSelector) node);
        } else {
            System.out.println("Unknown selector type: " + node.getClass().getName());
        }
    }

    private void checkChildNodes(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
    }

    private void checkStylesheet(Stylesheet node) {
        variableTypes.add(new HashMap<>());
        checkChildNodes(node);
        variableTypes.removeLast();
    }

    private void checkStylerule(Stylerule node) {
        variableTypes.add(new HashMap<>());
        checkChildNodes(node);
        variableTypes.removeLast();
    }

    //--------------Variables--------------
    private void checkVariableAssignment(VariableAssignment node) {
        variableTypes.getLast().put(node.name.name, getVariableType(node.expression));
        checkChildNodes(node);
        System.out.println(variableTypes);
    }

    private void checkVariableReference(VariableReference node) {
        if (variableTypes.stream().noneMatch(scope -> scope.containsKey(node.name))) {
            System.out.println("Variable " + node.name + " not declared");
        }
        checkChildNodes(node);
    }

    //--------------Declarations--------------
    private void checkDeclaration(Declaration node) {
        if (variableTypes.stream().noneMatch(scope -> scope.containsKey(node.property.name))) {
            System.out.println("Property " + node.property.name + " not declared");
        }
        checkChildNodes(node);
    }

    //--------------Selectors--------------
    private void checkTagSelector(TagSelector node) {
        //skip
    }

    private void checkClassSelector(ClassSelector node) {
        //skip
    }

    private void checkIdSelector(IdSelector node) {
        //skip
    }

    //--------------Expressions--------------
    private void checkOperation(Operation node) {
        throw new UnsupportedOperationException("Not implemented");
    }

    //--------------IF support--------------
    private void checkIfClause(IfClause node) {
        throw new UnsupportedOperationException("Not implemented");
    }

    //--------------Literals--------------

    /**
     * Returns the type of the given expression.
     *
     * @param expression The expression to get the type of.
     * @see Expression
     */
    private ExpressionType getVariableType(Expression expression) {
        if (expression instanceof VariableReference) {
            return variableTypes.getLast().get(((VariableReference) expression).name);
        } else if (expression instanceof Operation) {
            return getOperationType((Operation) expression);
        } else if (expression instanceof Literal) {
            return getLiteralType((Literal) expression);
        } else {
            throw new UnsupportedOperationException("Unknown expression type: " + expression.getClass().getName());
        }
    }

    private ExpressionType getOperationType(Operation operation) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the type of the given literal.
     *
     * @param literal The literal to get the type of.
     * @see Literal
     */
    private ExpressionType getLiteralType(Literal literal) {
        if (literal instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (literal instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (literal instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (literal instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (literal instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        }

        throw new UnsupportedOperationException("Unknown literal type: " + literal.getClass().getName());
    }
}
