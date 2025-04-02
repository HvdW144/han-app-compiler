package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;
    private final EvaluateExpressionHelper evaluateExpressionHelper = new EvaluateExpressionHelper();

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();

        //start applying from the root node
        applyNode(ast.root, null);
    }

    private void applyNode(ASTNode node, ASTNode parentNode) {
        if (node instanceof Stylesheet) {
            applyStylesheet((Stylesheet) node);
        } else if (node instanceof Stylerule) {
            applyStylerule((Stylerule) node);
        } else if (node instanceof VariableAssignment) {
            applyVariableAssignment((VariableAssignment) node);
        } else if (node instanceof VariableReference) {
            applyVariableReference((VariableReference) node);
        } else if (node instanceof Declaration) {
            applyDeclaration((Declaration) node);
        } else if (node instanceof PropertyName) {
            applyPropertyName((PropertyName) node);
        } else if (node instanceof Expression) {
            applyExpression((Expression) node);
        } else if (node instanceof IfClause) {
            applyIfClause((IfClause) node, parentNode);
        } else if (node instanceof ElseClause) {
            applyElseClause((ElseClause) node);
        } else if (node instanceof Selector) {
            applySelectorNode((Selector) node);
        } else {
            System.out.println("Unknown node type: " + node.getClass().getName());
        }
    }

    private void applySelectorNode(Selector selector) {
        //skip all selectors
    }

    private void applyChildNodes(ASTNode parentNode) {
        for (ASTNode child : parentNode.getChildren()) {
            applyNode(child, parentNode);
        }
    }

    private void applyStylesheet(Stylesheet node) {
        //add scope
        variableValues.add(new HashMap<>());

        applyChildNodes(node);

        //remove scope
        variableValues.removeLast();
    }

    private void applyStylerule(Stylerule node) {
        //add scope
        variableValues.add(new HashMap<>());

        applyChildNodes(node);

        //remove scope
        variableValues.removeLast();
    }

    private void applyVariableAssignment(VariableAssignment node) {
        //evaluate expression
        node.expression = evaluateExpressionHelper.evalExpression(node.expression, variableValues);
        //add variable value to the last scope
        variableValues.getLast().put(node.name.name, (Literal) node.expression);

        applyChildNodes(node);
    }

    private void applyVariableReference(VariableReference node) {
        //skip
    }

    private void applyDeclaration(Declaration node) {
        //evaluate expression
        node.expression = evaluateExpressionHelper.evalExpression(node.expression, variableValues);

        applyChildNodes(node);
    }

    private void applyPropertyName(PropertyName node) {
        //skip
    }

    private void applyExpression(Expression node) {
        applyChildNodes(node);
    }

    //--------------IF support--------------
    private void applyIfClause(IfClause node, ASTNode parentNode) {
        BoolLiteral result = (BoolLiteral) evaluateExpressionHelper.evalExpression(node.conditionalExpression, variableValues);

        if (result.value) {
            node.elseClause = null;
        } else {
            if (node.elseClause != null) {
                node.body = node.elseClause.body;
            } else {
                node.body = new ArrayList<>();
            }
        }

        //add scope
        variableValues.add(new HashMap<>());

        applyChildNodes(node);

        //add if-clause body to parent
        for (ASTNode child : node.body) {
            parentNode.addChild(child);
        }

        //remove if-clause from parent
        parentNode.removeChild(node);

        //remove scope
        variableValues.removeLast();
    }

    private void applyElseClause(ElseClause node) {
        //add scope
        variableValues.add(new HashMap<>());

        applyChildNodes(node);

        //remove scope
        variableValues.removeLast();
    }
}
