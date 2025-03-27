package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;


public class Checker {
    private static final String[] PROPERTIES = new String[]{"width", "height", "color", "background-color"};

    // teacher said we could use a normal linked list for this, so I changed it
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;
    private final ExpressionTypeHelper expressionTypeHelper = new ExpressionTypeHelper();

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
        } else if (node instanceof Expression) {
            checkExpression((Expression) node);
        } else if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
        } else if (node instanceof ElseClause) {
            checkElseClause((ElseClause) node);
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
        variableTypes.getLast().put(node.name.name, expressionTypeHelper.getVariableType(node.expression, variableTypes));
        checkChildNodes(node);
    }

    private void checkVariableReference(VariableReference node) {
        if (variableTypes.stream().noneMatch(scope -> scope.containsKey(node.name))) {
            node.setError("Variable " + node.name + " not declared");
        }
        checkChildNodes(node);
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
        checkChildNodes(node);

        //TODO: might break on chained operations
        if ((node.lhs instanceof Literal || node.lhs instanceof VariableReference) && (node.rhs instanceof Literal || node.rhs instanceof VariableReference)) {
            //check if no colors are used in operations
            if (node.lhs instanceof ColorLiteral || node.rhs instanceof ColorLiteral) {
                node.setError("Operations with colors are not allowed");
                return;
            }
            if (node.lhs instanceof VariableReference) {
                if (expressionTypeHelper.findVariableTypeOfReference((VariableReference) node.lhs, variableTypes) == ExpressionType.COLOR) {
                    node.setError("Operations with colors are not allowed");
                    return;
                }
            }
            if (node.rhs instanceof VariableReference) {
                if (expressionTypeHelper.findVariableTypeOfReference((VariableReference) node.rhs, variableTypes) == ExpressionType.COLOR) {
                    node.setError("Operations with colors are not allowed");
                    return;
                }
            }

            //check if multiply operations contain at least one scalar
            if (!(node instanceof MultiplyOperation)) {
                if (expressionTypeHelper.getVariableType(node.lhs, variableTypes) != expressionTypeHelper.getVariableType(node.rhs, variableTypes)) {
                    //TODO: gives double errors
                    node.setError("Operation between different types");
                }
            } else if (!(node.lhs instanceof ScalarLiteral || node.rhs instanceof ScalarLiteral)) {
                node.setError("Multiply operations should contain at least one scalar");
            }
        }
    }

    private void checkExpression(Expression node) {
        checkChildNodes(node);
        throw new UnsupportedOperationException("not implemented");
    }

    //--------------IF support--------------
    private void checkIfClause(IfClause node) {
        //add scope
        variableTypes.add(new HashMap<>());

        //TODO: fix double named variables in different scopes
        if (!(node.conditionalExpression instanceof BoolLiteral)) {
            if (node.conditionalExpression instanceof VariableReference) {
                if (variableTypes.stream().anyMatch(scope -> scope.containsKey(((VariableReference) node.conditionalExpression).name))) {
                    if (variableTypes.stream().noneMatch(scope -> scope.get(((VariableReference) node.conditionalExpression).name) == ExpressionType.BOOL)) {
                        node.setError("Variable " + ((VariableReference) node.conditionalExpression).name + " is not a boolean");
                    }
                }
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
