// Generated from crux\pt\Crux.g4 by ANTLR 4.7.2
package crux.pt;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CruxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CruxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(CruxParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#designator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesignator(CruxParser.DesignatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(CruxParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp0(CruxParser.Op0Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp1(CruxParser.Op1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp2(CruxParser.Op2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expr0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr0(CruxParser.Expr0Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expr1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr1(CruxParser.Expr1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expr2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr2(CruxParser.Expr2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expr3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr3(CruxParser.Expr3Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#callExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallExpr(CruxParser.CallExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(CruxParser.ExprListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(CruxParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(CruxParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(CruxParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#arrayDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayDecl(CruxParser.ArrayDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#functionDefn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDefn(CruxParser.FunctionDefnContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(CruxParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#declList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclList(CruxParser.DeclListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#assignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(CruxParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#callStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallStmt(CruxParser.CallStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(CruxParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#loopStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStmt(CruxParser.LoopStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#breakStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(CruxParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#continueStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(CruxParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#returnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(CruxParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(CruxParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#stmtList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtList(CruxParser.StmtListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#stmtBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtBlock(CruxParser.StmtBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(CruxParser.ProgramContext ctx);
}