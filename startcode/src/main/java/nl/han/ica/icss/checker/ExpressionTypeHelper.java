package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class ExpressionTypeHelper {
    /**
     * Returns the type of the given expression.
     *
     * @param expression    The expression to get the type of.
     * @param variableTypes The variable types to use when determining the type of a variable reference.
     * @see Expression
     */
    public ExpressionType getVariableType(Expression expression, LinkedList<HashMap<String, ExpressionType>> variableTypes) {
        if (expression instanceof VariableReference) {
            return findVariableTypeOfReference((VariableReference) expression, variableTypes);
        } else if (expression instanceof Operation) {
            return getOperationType((Operation) expression, variableTypes);
        } else if (expression instanceof Literal) {
            return getLiteralType((Literal) expression);
        } else {
            throw new UnsupportedOperationException("Unknown expression type: " + expression.getClass().getName());
        }
    }

    /**
     * Returns the type of the given operation.
     *
     * @param operation     The operation to get the type of.
     * @param variableTypes The variable types to use when determining the type of a variable reference.
     * @return The type of the operation.
     */
    private ExpressionType getOperationType(Operation operation, LinkedList<HashMap<String, ExpressionType>> variableTypes) {
        ExpressionType left = getVariableType(operation.lhs, variableTypes);
        ExpressionType right = getVariableType(operation.rhs, variableTypes);

        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            return left;
        } else if (operation instanceof MultiplyOperation) {
            return left != ExpressionType.SCALAR ? left : right;
        } else {
            throw new UnsupportedOperationException("Unknown operation type: " + operation.getClass().getName());
        }
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

    public ExpressionType findVariableTypeOfReference(VariableReference variableReference, LinkedList<HashMap<String, ExpressionType>> variableTypes) {
        ListIterator<HashMap<String, ExpressionType>> iterator = variableTypes.listIterator(variableTypes.size());


        while (iterator.hasPrevious()) {
            HashMap<String, ExpressionType> currentScope = iterator.previous();
            if (currentScope.containsKey(variableReference.name)) {
                return currentScope.get(variableReference.name);
            }
        }
        return null;
    }
}
