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

	//--------------Variables--------------
//	@Override
//	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
//		currentContainer.push(new VariableAssignment());
//		super.enterVariableAssignment(ctx);
//	}
//
//	@Override
//	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
//		ASTNode current = currentContainer.pop();
//		currentContainer.peek().addChild(current);
//		super.exitVariableAssignment(ctx);
//	}

	//--------------Declarations--------------
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
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		currentContainer.push(new PropertyName(ctx.getText()));
		super.enterPropertyName(ctx);
	}

	@Override
	public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitPropertyName(ctx);
	}

	//--------------Selectors--------------
	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		currentContainer.push(new TagSelector(ctx.getText()));
		super.enterTagSelector(ctx);
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitTagSelector(ctx);
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.push(new ClassSelector(ctx.getText()));
		super.enterClassSelector(ctx);
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitClassSelector(ctx);
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.push(new IdSelector(ctx.getText()));
		super.enterIdSelector(ctx);
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitIdSelector(ctx);
	}

	//--------------Expressions--------------

	//--------------IF support--------------

	//--------------Literals--------------
	// literal itself is not handled, as all it's children are handled (predefined by startcode)

	@Override
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		currentContainer.push(new BoolLiteral(ctx.getText()));
		super.enterBoolLiteral(ctx);
	}

	@Override
	public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitBoolLiteral(ctx);
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		currentContainer.push(new PixelLiteral(ctx.getText()));
		super.enterPixelLiteral(ctx);
	}

	@Override
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitPixelLiteral(ctx);
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		currentContainer.push(new PercentageLiteral(ctx.getText()));
		super.enterPercentageLiteral(ctx);
	}

	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitPercentageLiteral(ctx);
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		currentContainer.push(new ScalarLiteral(ctx.getText()));
		super.enterScalarLiteral(ctx);
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitScalarLiteral(ctx);
	}

	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		currentContainer.push(new ColorLiteral(ctx.getText()));
		super.enterColorLiteral(ctx);
	}

	@Override
	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ASTNode current = currentContainer.pop();
		currentContainer.peek().addChild(current);
		super.exitColorLiteral(ctx);
	}

	public AST getAST() {
		return ast;
	}
}