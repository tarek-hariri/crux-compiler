// Generated from crux\pt\Crux.g4 by ANTLR 4.7.2
package crux.pt;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CruxParser}.
 */
public interface CruxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(CruxParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(CruxParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#designator}.
	 * @param ctx the parse tree
	 */
	void enterDesignator(CruxParser.DesignatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#designator}.
	 * @param ctx the parse tree
	 */
	void exitDesignator(CruxParser.DesignatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(CruxParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(CruxParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#op0}.
	 * @param ctx the parse tree
	 */
	void enterOp0(CruxParser.Op0Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#op0}.
	 * @param ctx the parse tree
	 */
	void exitOp0(CruxParser.Op0Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#op1}.
	 * @param ctx the parse tree
	 */
	void enterOp1(CruxParser.Op1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#op1}.
	 * @param ctx the parse tree
	 */
	void exitOp1(CruxParser.Op1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#op2}.
	 * @param ctx the parse tree
	 */
	void enterOp2(CruxParser.Op2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#op2}.
	 * @param ctx the parse tree
	 */
	void exitOp2(CruxParser.Op2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#expr0}.
	 * @param ctx the parse tree
	 */
	void enterExpr0(CruxParser.Expr0Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#expr0}.
	 * @param ctx the parse tree
	 */
	void exitExpr0(CruxParser.Expr0Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#expr1}.
	 * @param ctx the parse tree
	 */
	void enterExpr1(CruxParser.Expr1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#expr1}.
	 * @param ctx the parse tree
	 */
	void exitExpr1(CruxParser.Expr1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#expr2}.
	 * @param ctx the parse tree
	 */
	void enterExpr2(CruxParser.Expr2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#expr2}.
	 * @param ctx the parse tree
	 */
	void exitExpr2(CruxParser.Expr2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#expr3}.
	 * @param ctx the parse tree
	 */
	void enterExpr3(CruxParser.Expr3Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#expr3}.
	 * @param ctx the parse tree
	 */
	void exitExpr3(CruxParser.Expr3Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#callExpr}.
	 * @param ctx the parse tree
	 */
	void enterCallExpr(CruxParser.CallExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#callExpr}.
	 * @param ctx the parse tree
	 */
	void exitCallExpr(CruxParser.CallExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#exprList}.
	 * @param ctx the parse tree
	 */
	void enterExprList(CruxParser.ExprListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#exprList}.
	 * @param ctx the parse tree
	 */
	void exitExprList(CruxParser.ExprListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(CruxParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(CruxParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(CruxParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(CruxParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(CruxParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(CruxParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#arrayDecl}.
	 * @param ctx the parse tree
	 */
	void enterArrayDecl(CruxParser.ArrayDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#arrayDecl}.
	 * @param ctx the parse tree
	 */
	void exitArrayDecl(CruxParser.ArrayDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#functionDefn}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefn(CruxParser.FunctionDefnContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#functionDefn}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefn(CruxParser.FunctionDefnContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterDecl(CruxParser.DeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitDecl(CruxParser.DeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#declList}.
	 * @param ctx the parse tree
	 */
	void enterDeclList(CruxParser.DeclListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#declList}.
	 * @param ctx the parse tree
	 */
	void exitDeclList(CruxParser.DeclListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#assignStmt}.
	 * @param ctx the parse tree
	 */
	void enterAssignStmt(CruxParser.AssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#assignStmt}.
	 * @param ctx the parse tree
	 */
	void exitAssignStmt(CruxParser.AssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#callStmt}.
	 * @param ctx the parse tree
	 */
	void enterCallStmt(CruxParser.CallStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#callStmt}.
	 * @param ctx the parse tree
	 */
	void exitCallStmt(CruxParser.CallStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(CruxParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(CruxParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#loopStmt}.
	 * @param ctx the parse tree
	 */
	void enterLoopStmt(CruxParser.LoopStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#loopStmt}.
	 * @param ctx the parse tree
	 */
	void exitLoopStmt(CruxParser.LoopStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(CruxParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(CruxParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#continueStmt}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(CruxParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#continueStmt}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(CruxParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(CruxParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(CruxParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(CruxParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(CruxParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#stmtList}.
	 * @param ctx the parse tree
	 */
	void enterStmtList(CruxParser.StmtListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#stmtList}.
	 * @param ctx the parse tree
	 */
	void exitStmtList(CruxParser.StmtListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#stmtBlock}.
	 * @param ctx the parse tree
	 */
	void enterStmtBlock(CruxParser.StmtBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#stmtBlock}.
	 * @param ctx the parse tree
	 */
	void exitStmtBlock(CruxParser.StmtBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CruxParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CruxParser.ProgramContext ctx);
}