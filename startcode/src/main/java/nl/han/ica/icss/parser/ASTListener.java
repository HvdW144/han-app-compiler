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

	public HashMap<String, String> getVariables() {
		return variables;
	}
    
}