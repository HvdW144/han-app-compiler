package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class ExpressionTypeHelper {
    /**
     * Returns the type of the given expression.
     *
     * @param expression The expression to get the type of.
     * @param variableTypes The variable types to use when determining the type of a variable reference.
     * @see Expression
     */
    public ExpressionType getVariableType(Expression expression, LinkedList<HashMap<String, ExpressionType>> variableTypes) {
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
