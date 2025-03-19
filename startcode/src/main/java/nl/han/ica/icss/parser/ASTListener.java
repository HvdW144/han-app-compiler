package nl.han.ica.icss.parser;

import java.util.HashMap;
import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.push(new Stylesheet());
		super.enterStylesheet(ctx);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		ASTNode current = currentContainer.pop();
		ast = new AST((Stylesheet) current);
//		super.exitStylesheet(ctx); //not working?
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.push(new Stylerule());
		super.enterStylerule(ctx);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitStylerule(ctx);
	}

	@Override
	public void enterTagselector(ICSSParser.TagselectorContext ctx) {
		currentContainer.push(new TagSelector(ctx.getText()));
		super.enterTagselector(ctx);
	}

	@Override
	public void exitTagselector(ICSSParser.TagselectorContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitTagselector(ctx);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration());
		super.enterDeclaration(ctx);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitDeclaration(ctx);
	}

	@Override
	public void enterProperty(ICSSParser.PropertyContext ctx) {
		currentContainer.push(new PropertyName(ctx.getText()));
		super.enterProperty(ctx);
	}

	@Override
	public void exitProperty(ICSSParser.PropertyContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitProperty(ctx);
	}



//	@Override
//	public void enterVariableassignment(ICSSParser.VariableassignmentContext ctx) {
//		currentContainer.push(new VariableAssignment());
//		super.enterVariableassignment(ctx);
//	}
//
//	@Override
//	public void exitVariableassignment(ICSSParser.VariableassignmentContext ctx) {
//		ASTNode current = currentContainer.pop();
//		currentContainer.peek().addChild(current);
//		super.exitVariableassignment(ctx);
//	}

	public AST getAST() {
		return ast;
	}
}