package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class IfCheckHelper {

    public void checkVariableReferenceInScopes(IfClause node, LinkedList<HashMap<String, ExpressionType>> variableTypes) {
        for (Iterator<HashMap<String, ExpressionType>> it = variableTypes.descendingIterator(); it.hasNext(); ) {
            HashMap<String, ExpressionType> scope = it.next();
            if (scope.containsKey(((VariableReference) node.conditionalExpression).name)) {
                if (scope.get(((VariableReference) node.conditionalExpression).name) != ExpressionType.BOOL) {
                    node.setError("Variable " + ((VariableReference) node.conditionalExpression).name + " is not a boolean");
                }
                break; // Stop at first found
            }
        }
    }
}
