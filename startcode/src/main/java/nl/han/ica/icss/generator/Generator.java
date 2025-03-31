package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {

    public String generate(AST ast) {
        return generateNode(ast.root);
    }

    private String generateNode(ASTNode node) {
        StringBuilder resultString = new StringBuilder();

        if (node instanceof Stylesheet) {
            resultString.append(generateStylesheet((Stylesheet) node));
        } else if (node instanceof Stylerule) {
            resultString.append(generateStylerule((Stylerule) node));
        } else if (node instanceof Declaration) {
            resultString.append(generateDeclaration((Declaration) node));
        } else if (node instanceof PropertyName) {
            resultString.append(generatePropertyName((PropertyName) node));
        } else if (node instanceof Literal) {
            resultString.append(generateExpression((Expression) node));
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
        StringBuilder resultString = new StringBuilder();
        //TODO: only one selector working for now
        for (Selector selector : node.selectors) {
            resultString.append(generateSelectorNode(selector));
        }
        resultString.append(" {\n");
        for (ASTNode child : node.body) {
            //indenting
            resultString.append("\t");
            resultString.append(generateNode(child));
        }
        resultString.append("}\n\n");
        return resultString.toString();
    }

    private String generateDeclaration(Declaration node) {
        StringBuilder resultString = new StringBuilder();
        resultString.append(generatePropertyName(node.property));
        resultString.append(": ");
        resultString.append(generateExpression(node.expression));
        resultString.append(";\n");
        return resultString.toString();
    }

    private String generatePropertyName(PropertyName node) {
        return node.name;
    }

    private String generateExpression(Expression node) {
        if (node instanceof PixelLiteral) {
            return ((PixelLiteral) node).value + "px";
        } else if (node instanceof PercentageLiteral) {
            return ((PercentageLiteral) node).value + "%";
        } else if (node instanceof ColorLiteral) {
            return ((ColorLiteral) node).value;
        } else {
            throw new IllegalArgumentException("Unknown expression type: " + node.getClass().getName() + "at Generator");
        }
    }

    private String generateSelectorNode(Selector node) {
        if (node instanceof TagSelector) {
            return ((TagSelector) node).tag;
        } else if (node instanceof IdSelector) {
            return ((IdSelector) node).id;
        } else if (node instanceof ClassSelector) {
            return ((ClassSelector) node).cls;
        }
        throw new IllegalArgumentException("Unknown selector type: " + node.getClass().getName() + "at Generator");
    }


}
