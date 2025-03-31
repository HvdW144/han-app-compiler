package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {

    public String generate(AST ast) {
        System.out.println(generateNode(ast.root));
        return generateNode(ast.root);
    }

    private String generateNode(ASTNode node) {
        StringBuilder resultString = new StringBuilder();

        if (node instanceof Stylesheet) {
            resultString.append(generateStylesheet((Stylesheet) node));
        } else if (node instanceof Stylerule) {
            resultString.append(generateStylerule((Stylerule) node));
        } else if (node instanceof Declaration) {
            return generateDeclaration((Declaration) node);
        } else if (node instanceof PropertyName) {
            return generatePropertyName((PropertyName) node);
        } else if (node instanceof Expression) {
            return generateExpression((Expression) node);
        } else if (node instanceof Selector) {
            resultString.append(generateSelectorNode((Selector) node));
        }

        return resultString.toString();
    }

    private String generateStylesheet(Stylesheet node) {
        StringBuilder resultString = new StringBuilder();
        for (ASTNode child : node.getChildren()) {
            resultString.append(generateNode(child));
        }
        return resultString.toString();
    }

    private String generateStylerule(Stylerule node) {
        return "";
    }

    private String generateDeclaration(Declaration node) {
        return "";
    }

    private String generatePropertyName(PropertyName node) {
        return "";
    }

    private String generateExpression(Expression node) {
        return "";
    }

    private String generateSelectorNode(Selector node) {
        if (node instanceof TagSelector) {
            return ((TagSelector) node).tag;
        } else if (node instanceof IdSelector) {
            return "#" + ((IdSelector) node).id;
        } else if (node instanceof ClassSelector) {
            return "." + ((ClassSelector) node).cls;
        }
        throw new IllegalArgumentException("Unknown selector type: " + node.getClass().getName() + "at Generator");
    }


}
