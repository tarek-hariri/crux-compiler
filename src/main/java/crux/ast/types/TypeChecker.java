package crux.ast.types;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NullNodeVisitor;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

/**
 * This class will associate types with the AST nodes from Stage 2
 */
public final class TypeChecker {
  private final ArrayList<String> errors = new ArrayList<>();

  public ArrayList<String> getErrors() {
    return errors;
  }

  public void check(DeclarationList ast) {
    var inferenceVisitor = new TypeInferenceVisitor();
    inferenceVisitor.visit(ast);
  }

  /**
   * Helper function, should be used to add error into the errors array
   */
  private void addTypeError(Node n, String message) {
    errors.add(String.format("TypeError%s[%s]", n.getPosition(), message));
  }

  /**
   * Helper function, should be used to record Types if the Type is an ErrorType then it will call
   * addTypeError
   */
  private void setNodeType(Node n, Type ty) {
    ((BaseNode) n).setType(ty);
    if (ty.getClass() == ErrorType.class) {
      var error = (ErrorType) ty;
      addTypeError(n, error.getMessage());
    }
  }

  /**
   * Helper to retrieve Type from the map
   */
  public Type getType(Node n) {
    return ((BaseNode) n).getType();
  }


  /**
   * This calls will visit each AST node and try to resolve it's type with the help of the
   * symbolTable.
   */
  private final class TypeInferenceVisitor extends NullNodeVisitor<Void> {

    private Symbol currentFunctionSymbol = null;
    private Type currentFunctionReturnType = null;
    private boolean lastStatementReturns = false;

    private boolean hasBreak = false;

    private boolean getLastStatementReturns() { return lastStatementReturns; }

    private Type getCurrentFunctionReturnType() {
      return currentFunctionReturnType;
    }

    @Override
    public Void visit(ArrayAccess access) {
      access.getIndex().accept(this);
      Type indexType = getType(access.getIndex());
      setNodeType(access, access.getBase().getType().index(indexType));

      return null;
    }

    @Override
    public Void visit(VarAccess vaccess) {
      setNodeType(vaccess, vaccess.getSymbol().getType());

      return null;
    }


    @Override
    public Void visit(ArrayDeclaration arrayDeclaration) {
      Type arrType = arrayDeclaration.getSymbol().getType();

      if(arrType instanceof ArrayType){
        Type baseType = ((ArrayType) arrType).getBase();
        if(baseType instanceof IntType || baseType instanceof BoolType){
          lastStatementReturns = false;
        }
        else{
          addTypeError(arrayDeclaration, "Base type of array is not valid (bool or int).");
        }
      }
      else{
        addTypeError(arrayDeclaration, "Declared type is not an array type");
      }

      return null;
    }

    @Override
    public Void visit(Assignment assignment) {
      for(Node child : assignment.getChildren()){
        child.accept(this);
      }

      setNodeType(assignment.getLocation(), getType(assignment.getValue()));
      lastStatementReturns = false;
      return null;
    }

    @Override
    public Void visit(Break brk) {
      hasBreak = true;
      lastStatementReturns = false;

      return null;
    }

    @Override
    public Void visit(Call call) {
      TypeList typeList = new TypeList();

      for(Expression arg : call.getArguments()){
        arg.accept(this);
        typeList.append(getType(arg));
      }

      Type calleeType = call.getCallee().getType();
      if(calleeType instanceof ErrorType){
        setNodeType(call, calleeType);
        lastStatementReturns = false;
        return null;
      }


      setNodeType(call, ((FuncType) calleeType).call(typeList));
      lastStatementReturns = false;
      return null;
    }

    private String typeComment(Type type) {
      if (type instanceof IntType) {
        return "/*int*/ ";
      } else if (type instanceof BoolType) {
        return "/*bool*/ ";
      } else {
        return "";
      }
    }


    @Override
    public Void visit(Continue cont) {
      return null;
    }

    @Override
    public Void visit(DeclarationList declarationList) {
      for (Declaration decl : declarationList.getDeclarations()) {
        decl.accept(this);
      }
      return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      currentFunctionReturnType = ((FuncType) functionDefinition.getSymbol().getType()).getRet();
      currentFunctionSymbol = functionDefinition.getSymbol();

      if(currentFunctionSymbol.getName().equals("main")){
        if(!(currentFunctionReturnType instanceof VoidType) || !(functionDefinition.getParameters().isEmpty())){
          addTypeError(functionDefinition, "Main invalid definition.");
        }
        for(Symbol param : functionDefinition.getParameters()){
          if(!(param.getType() instanceof IntType) && !(param.getType() instanceof BoolType)){
            addTypeError(functionDefinition, "Parameters are not of valid type (int or bool)");
          }
        }
        functionDefinition.getStatements().accept(this);
      }
      else {
        functionDefinition.getStatements().accept(this);
        if (!(currentFunctionReturnType instanceof VoidType) && !lastStatementReturns) {
          addTypeError(functionDefinition, "No return exists!");
        }
      }

      visit(functionDefinition.getStatements());
      return null;
    }

    @Override
    public Void visit(IfElseBranch ifElseBranch) {
      ifElseBranch.getCondition().accept(this);
      if(!(getType(ifElseBranch.getCondition()) instanceof BoolType)){
        addTypeError(ifElseBranch, "Condition is not of BoolType");
      }
      if(ifElseBranch.getThenBlock()!=null){
        ifElseBranch.getThenBlock().accept(this);
      }
      boolean lastStatementReturnsThen = lastStatementReturns;

      lastStatementReturns = false;
      if(ifElseBranch.getElseBlock()!=null){
        ifElseBranch.getElseBlock().accept(this);
        if(lastStatementReturnsThen && lastStatementReturns){
          lastStatementReturns = true;
        }
        else{
          lastStatementReturns = false;
        }
      }
      else{
        lastStatementReturns = lastStatementReturnsThen;
      }



      return null;
    }

    @Override
    public Void visit(LiteralBool literalBool) {
      setNodeType(literalBool, new BoolType());
      return null;
    }

    @Override
    public Void visit(LiteralInt literalInt) {
      setNodeType(literalInt, new IntType());
      return null;
    }

    @Override
    public Void visit(Loop loop) {
      hasBreak = false;

      for(Statement stmt : loop.getBody().getStatements()){
        stmt.accept(this);
        if(hasBreak==false && lastStatementReturns==false){
          addTypeError(loop, "Infinite loop!");
          break;
        }

        // Nested loop handling ?
      }

      return null;
    }

    @Override
    public Void visit(OpExpr op) {

      op.getLeft().accept(this);

      // right will be null if op is LOGIC_NOT
      if(op.getRight() != null) {
        op.getRight().accept(this);
      }

      Type out = null;

      switch( op.getOp() ){
        case ADD:
          out = getType(op.getLeft()).add(getType(op.getRight()));
          break;
        case SUB:
          out = getType(op.getLeft()).sub(getType(op.getRight()));
          break;
        case MULT:
          out = getType(op.getLeft()).mul(getType(op.getRight()));
          break;
        case DIV:
          out = getType(op.getLeft()).div(getType(op.getRight()));
          break;
        case EQ:
        case NE:
        case GE:
        case GT:
        case LE:
        case LT:
          out = getType(op.getLeft()).compare(getType(op.getRight()));
          break;
        case LOGIC_AND:
          out = getType(op.getLeft()).and(getType(op.getRight()));
          break;
        case LOGIC_OR:
          out = getType(op.getLeft()).or(getType(op.getRight()));
          break;
        case LOGIC_NOT:
          out = getType(op.getLeft()).not();
          break;
      }
      setNodeType(op, out);

      return null;
    }

    @Override
    public Void visit(Return ret) {
      ret.getValue().accept(this);
      if(!getType(ret.getValue()).equivalent(this.getCurrentFunctionReturnType())){
        addTypeError(ret, "Return type doesn't match function return type");
      }
      lastStatementReturns = true;



      return null;
    }

    @Override
    public Void visit(StatementList statementList) {
      lastStatementReturns = false;
      for( Statement stmt : statementList.getStatements()){
        if(lastStatementReturns){
          addTypeError(statementList, "Unreachable statements");
        }
        stmt.accept(this);
      }
      return null;
    }

    @Override
    public Void visit(VariableDeclaration variableDeclaration) {
      Type type = variableDeclaration.getSymbol().getType();
      if(type instanceof BoolType || type instanceof IntType){
        lastStatementReturns = false;
      }
      else{
        addTypeError(variableDeclaration, "Type is not valid (bool or int).");
      }
      return null;
    }
  }
}

