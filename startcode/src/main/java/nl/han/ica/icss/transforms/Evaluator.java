package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;
    private final EvaluateExpressionHelper evaluateExpressionHelper = new EvaluateExpressionHelper();

    public Evaluator() {
        //variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();

        //start applying from the root node
        applyNode(ast.root);
    }

    private void applyNode(ASTNode node) {
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
        } else if (node instanceof Operation) {
            applyOperation((Operation) node);
        } else if (node instanceof Expression) {
            applyExpression((Expression) node);
        } else if (node instanceof IfClause) {
            applyIfClause((IfClause) node);
        } else if (node instanceof ElseClause) {
            applyElseClause((ElseClause) node);
        } else if (node instanceof Selector) {
            applySelectorNode((Selector) node);
        } else {
            System.out.println("Unknown node type: " + node.getClass().getName());
        }
    }

    private void applySelectorNode(Selector selector) {
    }

    private void applyChildNodes(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            applyNode(child);
        }
    }

    private void applyStylesheet(Stylesheet node) {
        //add scope
        variableValues.push(new HashMap<>());

        applyChildNodes(node);

        //remove scope
        variableValues.removeLast();
    }

    private void applyStylerule(Stylerule node) {
        //add scope
        variableValues.push(new HashMap<>());

        applyChildNodes(node);

        //remove scope
        variableValues.removeLast();
    }

    private void applyVariableAssignment(VariableAssignment node) {
        variableValues.getLast().put(
                node.name.name,
                evaluateExpressionHelper.evalExpression(node.expression, variableValues
                ));
        applyChildNodes(node);
    }

    private void applyVariableReference(VariableReference node) {
        applyChildNodes(node);
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evaluateExpressionHelper.evalExpression(node.expression, variableValues);
        applyChildNodes(node);
    }

    private void applyPropertyName(PropertyName node) {
        applyChildNodes(node);
    }

    private void applyOperation(Operation node) {
        applyChildNodes(node);
    }

    private void applyExpression(Expression node) {
        applyChildNodes(node);
    }

    //--------------IF support--------------
    private void applyIfClause(IfClause node) {
        //add scope
        variableValues.push(new HashMap<>());

        applyChildNodes(node);

        //remove scope
        variableValues.removeLast();
    }

    private void applyElseClause(ElseClause node) {
        //add scope
        variableValues.push(new HashMap<>());

        applyChildNodes(node);

        //remove scope
        variableValues.removeLast();
    }
}
