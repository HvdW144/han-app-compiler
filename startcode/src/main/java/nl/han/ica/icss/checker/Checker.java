package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;


public class Checker {

    // teacher said we could use a normal linked list for this, so I changed it
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        System.out.println(ast.root.getChildren());

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
        } else if (node instanceof Operation) {
            checkOperation((Operation) node);
        } else if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
        } else {
            System.out.println("Unknown node type: " + node.getClass().getName());
        }
    }

    private void checkStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
    }

    private void checkStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
    }

    private void checkVariableAssignment(VariableAssignment node) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void checkVariableReference(VariableReference node) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void checkDeclaration(Declaration node) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void checkOperation(Operation node) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void checkIfClause(IfClause node) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
