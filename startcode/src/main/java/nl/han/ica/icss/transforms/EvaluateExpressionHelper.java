package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class EvaluateExpressionHelper {
    public Literal evalExpression(Expression expression, LinkedList<HashMap<String, Literal>> variableValues) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        } else if (expression instanceof VariableReference) {
            assert variableValues.peek() != null;
            return variableValues.peek().get(((VariableReference) expression).name);
        } else if (expression instanceof AddOperation) {
            return evalAddOperation((AddOperation) expression, variableValues);
        } else if (expression instanceof SubtractOperation) {
            return evalSubtractOperation((SubtractOperation) expression, variableValues);
        } else if (expression instanceof MultiplyOperation) {
            return evalMultiplyOperation((MultiplyOperation) expression, variableValues);
        } else {
            return null;
        }
    }

    private Literal evalAddOperation(AddOperation operation, LinkedList<HashMap<String, Literal>> variableValues) {
        Literal left = evalExpression(operation.lhs, variableValues);
        Literal right = evalExpression(operation.rhs, variableValues);

        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value + ((ScalarLiteral) right).value);
        } else {
            operation.setError("Invalid add operation (checker should have caught this)");
            return null;
        }
    }

    private Literal evalSubtractOperation(SubtractOperation operation, LinkedList<HashMap<String, Literal>> variableValues) {
        Literal left = evalExpression(operation.lhs, variableValues);
        Literal right = evalExpression(operation.rhs, variableValues);

        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value - ((ScalarLiteral) right).value);
        } else {
            return null;
        }
    }

    private Literal evalMultiplyOperation(MultiplyOperation operation, LinkedList<HashMap<String, Literal>> variableValues) {
        Literal left = evalExpression(operation.lhs, variableValues);
        Literal right = evalExpression(operation.rhs, variableValues);

        // Pixel
        if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
        }
        if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
        }

        // Percentage
        if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
        }
        if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
        }

        // Scalar
        if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
        } else {
            operation.setError("Invalid multiply operation (checker should have caught this)");
            return null;
        }
    }

}
