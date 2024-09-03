package crux.ir;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.OpExpr.Operation;
import crux.ast.traversal.NodeVisitor;
import crux.ast.types.*;
import crux.ir.insts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class InstPair {
  Instruction start;
  Instruction end;
  LocalVar value;

  InstPair(Instruction start, Instruction end, LocalVar value) {
    this.start = start;
    this.end = end;
    this.value = value;
  }

  InstPair(Instruction start, Instruction end) {
    this(start, end, null);
  }
}

public final class ASTLower implements NodeVisitor<InstPair> {
  private Program mCurrentProgram = null;
  private Function mCurrentFunction = null;
  private Map<Symbol, LocalVar> mCurrentLocalVarMap = null;

  public ASTLower() {}

  public Program lower(DeclarationList ast) {
    mCurrentProgram = new Program();
    visit(ast);
    return mCurrentProgram;
  }

  @Override
  public InstPair visit(DeclarationList declarationList) {
    for (var declaration : declarationList.getChildren()) {
      if (declaration instanceof VariableDeclaration) {
        visit((VariableDeclaration) declaration);
      } else if (declaration instanceof ArrayDeclaration) {
        visit((ArrayDeclaration) declaration);
      } else if (declaration instanceof FunctionDefinition) {
        visit((FunctionDefinition) declaration);
      }
    }
    return null;
  }

  @Override
  public InstPair visit(FunctionDefinition functionDefinition) {
    var funcType = (FuncType) functionDefinition.getSymbol().getType();
    var function = new Function(functionDefinition.getSymbol().getName(), funcType);
    mCurrentFunction = function;
    mCurrentLocalVarMap = new HashMap<>();

    List<LocalVar> args = new ArrayList<>();
    for (var param : functionDefinition.getParameters()) {
      var localVar = mCurrentFunction.getTempVar(param.getType(), param.getName());
      mCurrentLocalVarMap.put(param, localVar);
      args.add(localVar);
    }
    mCurrentFunction.setArguments(args);
    mCurrentProgram.addFunction(mCurrentFunction);

    var body = functionDefinition.getStatements();
    Instruction startInst = new NopInst();
    Instruction endInst = startInst;
    if (body != null) {
      var bodyInstPair = visit(body);
      if (bodyInstPair != null) {
        startInst.setNext(0, bodyInstPair.start);
        endInst = bodyInstPair.end;
      }
    }

    if (!(funcType.getRet() instanceof VoidType)) {
      var returnVar = mCurrentFunction.getTempVar(funcType.getRet());
      var returnInst = new ReturnInst(returnVar);
      endInst.setNext(0, returnInst);
      endInst = returnInst;
    }

    mCurrentFunction.setStart(startInst);

    mCurrentFunction = null;
    mCurrentLocalVarMap = null;
    return new InstPair(startInst, endInst);
  }

  @Override
  public InstPair visit(StatementList statementList) {
    Instruction prevInst = new NopInst();
    Instruction startInst = prevInst;
    for (var statement : statementList.getChildren()) {
      InstPair instPair = null;
      if (statement instanceof VariableDeclaration) {
        instPair = visit((VariableDeclaration) statement);
      } else if (statement instanceof ArrayDeclaration) {
        instPair = visit((ArrayDeclaration) statement);
      } else if (statement instanceof Assignment) {
        instPair = visit((Assignment) statement);
      } else if (statement instanceof Call) {
        instPair = visit((Call) statement);
      } else if (statement instanceof IfElseBranch) {
        instPair = visit((IfElseBranch) statement);
      } else if (statement instanceof Loop) {
        instPair = visit((Loop) statement);
      } else if (statement instanceof Return) {
        instPair = visit((Return) statement);
      } else if (statement instanceof Break) {
        instPair = visit((Break) statement);
      } else if (statement instanceof Continue) {
        instPair = visit((Continue) statement);
      }

      if (instPair != null) {
        prevInst.setNext(0, instPair.start);
        prevInst = instPair.end;
      }
    }
    return new InstPair(startInst, prevInst);
  }

  @Override
  public InstPair visit(VariableDeclaration variableDeclaration) {
    Symbol symbol = variableDeclaration.getSymbol();
    Type type = symbol.getType();

    if (mCurrentFunction == null) { // Global variable
      if (!(type instanceof IntType || type instanceof BoolType)) {
        throw new IllegalArgumentException("Unsupported global variable type: " + type.getClass().getSimpleName());
      }

      Constant defaultValue = getDefaultInitialValue(type);
      GlobalDecl globalVar = new GlobalDecl(symbol, IntegerConstant.get(mCurrentProgram,1));
      mCurrentProgram.addGlobalVar(globalVar);
      AddressVar addressVar = new AddressVar(type, symbol.getName());
      LocalVar tempVar = new LocalVar(type);
      CopyInst copyInst = new CopyInst(tempVar, defaultValue);
      StoreInst storeInst = new StoreInst(tempVar, addressVar);

      return new InstPair(copyInst, storeInst);
    } else { // Local variable
      LocalVar localVar = mCurrentFunction.getTempVar(type, symbol.getName());
      mCurrentLocalVarMap.put(symbol, localVar);
      CopyInst copyInst = new CopyInst(localVar, getDefaultInitialValue(type));
      return new InstPair(copyInst, copyInst, localVar);
    }
  }

  private Constant getDefaultInitialValue(Type type) {
    if (type instanceof BoolType) {
      return BooleanConstant.get(mCurrentProgram, false);
    } else if (type instanceof IntType) {
      return IntegerConstant.get(mCurrentProgram, 0);
    }
    throw new IllegalArgumentException("Unsupported type for initialization");
  }

  @Override
  public InstPair visit(ArrayDeclaration arrayDeclaration) {
    Symbol symbol = arrayDeclaration.getSymbol();
    Type type = symbol.getType();

    if (!(type instanceof ArrayType)) {
      throw new IllegalStateException("Expected ArrayType but found " + type.getClass().getSimpleName());
    }

    ArrayType arrayType = (ArrayType) type;
    int length = (int) arrayType.getExtent();
    Constant defaultValue = getDefaultInitialValue(arrayType.getBase());

    if (mCurrentFunction == null) { // Global array
      GlobalDecl globalArray = new GlobalDecl(symbol, IntegerConstant.get(mCurrentProgram, length));
      mCurrentProgram.addGlobalVar(globalArray);

      Instruction startInst = new NopInst();
      Instruction prevInst = new NopInst();

      for (int i = 0; i < length; i++) {
        AddressVar arrayElementAddress = new AddressVar(arrayType.getBase(), symbol.getName() + "[" + i + "]");
        LocalVar indexVar = new LocalVar(new IntType(), "index" + i);
        CopyInst indexInst = new CopyInst(indexVar, IntegerConstant.get(mCurrentProgram, i));
        AddressAt addressAtInst = new AddressAt(arrayElementAddress, symbol, indexVar);
        LocalVar tempVar = new LocalVar(arrayType.getBase());
        CopyInst initValueInst = new CopyInst(tempVar, defaultValue);
        StoreInst storeInst = new StoreInst(tempVar, arrayElementAddress);

        startInst.setNext(0, indexInst);
        indexInst.setNext(0, addressAtInst);
        addressAtInst.setNext(0, initValueInst);
        initValueInst.setNext(0, storeInst);

        prevInst = storeInst;
      }

      return new InstPair(startInst, prevInst);
    } else { // Local array
      Instruction startInst = new NopInst();
      Instruction prevInst = startInst;

      for (int i = 0; i < length; i++) {
        AddressVar arrayElementAddress = new AddressVar(arrayType.getBase(), symbol.getName() + "[" + i + "]");
        LocalVar indexVar = new LocalVar(new IntType(), "index" + i);
        CopyInst indexInst = new CopyInst(indexVar, IntegerConstant.get(mCurrentProgram, i));
        AddressAt addressAtInst = new AddressAt(arrayElementAddress, symbol, indexVar);
        LocalVar tempVar = new LocalVar(arrayType.getBase());
        CopyInst initValueInst = new CopyInst(tempVar, defaultValue);
        StoreInst storeInst = new StoreInst(tempVar, arrayElementAddress);

        prevInst.setNext(0, indexInst);
        indexInst.setNext(0, addressAtInst);
        addressAtInst.setNext(0, initValueInst);
        initValueInst.setNext(0, storeInst);

        prevInst = storeInst;
      }

      return new InstPair(startInst, prevInst);
    }
  }

  @Override
  public InstPair visit(VarAccess name) {
    Symbol symbol = name.getSymbol();
    Type type = symbol.getType();

    if (mCurrentLocalVarMap.containsKey(symbol)) {
      LocalVar localVar = mCurrentLocalVarMap.get(symbol);
      NopInst nop1 = new NopInst();
      NopInst nop2 = new NopInst();
      nop1.setNext(0, nop2);
      return new InstPair(nop1, nop2, localVar);
    } else {
      AddressVar addressVar = new AddressVar(type, symbol.getName());
      LocalVar tempVar = mCurrentFunction.getTempVar(type);
      LoadInst loadInst = new LoadInst(tempVar, addressVar);
      return new InstPair(loadInst, loadInst, tempVar);
    }
  }

  @Override
  public InstPair visit(Assignment assignment) {
    InstPair valueInstPair = visit(assignment.getValue());
    Expression location = assignment.getLocation();
    Instruction startInst = valueInstPair.start;
    Instruction endInst = valueInstPair.end;
    LocalVar valueVar = valueInstPair.value;

    if (location instanceof VarAccess) {
      VarAccess varAccess = (VarAccess) location;
      Symbol symbol = varAccess.getSymbol();

      if (mCurrentLocalVarMap.containsKey(symbol)) {
        LocalVar localVar = mCurrentLocalVarMap.get(symbol);
        CopyInst copyInst = new CopyInst(localVar, valueVar);
        endInst.setNext(0, copyInst);
        endInst = copyInst;
      } else {
        AddressVar addressVar = new AddressVar(symbol.getType(), symbol.getName());
        StoreInst storeInst = new StoreInst(valueVar, addressVar);
        endInst.setNext(0, storeInst);
        endInst = storeInst;
      }
    } else if (location instanceof ArrayAccess) {
      ArrayAccess arrayAccess = (ArrayAccess) location;
      Symbol baseSymbol = arrayAccess.getBase();
      Type baseType = baseSymbol.getType();

      InstPair indexPair = visit(arrayAccess.getIndex());
      LocalVar offsetVar = indexPair.value;
      endInst.setNext(0, indexPair.start);
      endInst = indexPair.end;

      AddressVar tempAddress = mCurrentFunction.getTempAddressVar(((ArrayType) baseType).getBase());
      AddressAt addressAtInst = new AddressAt(tempAddress, baseSymbol, offsetVar);
      endInst.setNext(0, addressAtInst);
      endInst = addressAtInst;

      StoreInst storeInst = new StoreInst(valueVar, tempAddress);
      endInst.setNext(0, storeInst);
      endInst = storeInst;
    }

    return new InstPair(startInst, endInst);
  }

  @Override
  public InstPair visit(Call call) {
    Symbol calleeSymbol = call.getCallee();
    List<LocalVar> params = new ArrayList<>();
    Instruction startInst = new NopInst();
    Instruction lastInst = startInst;

    for (Expression arg : call.getArguments()) {
      InstPair argInstPair = visit(arg);
      if (argInstPair == null) {
        throw new IllegalStateException("Argument expression cannot be null");
      }
      params.add(argInstPair.value);
      lastInst.setNext(0, argInstPair.start);
      lastInst = argInstPair.end;
    }

    FuncType funcType = (FuncType) calleeSymbol.getType();
    LocalVar returnVar = null;
    if (!(funcType.getRet() instanceof VoidType)) {
      returnVar = mCurrentFunction.getTempVar(funcType.getRet());
    }

    CallInst callInst = new CallInst(returnVar, calleeSymbol, params);
    lastInst.setNext(0, callInst);

    return new InstPair(startInst.getNext(0) == null ? callInst : startInst, callInst, returnVar);
  }

  @Override
  public InstPair visit(OpExpr operation) {
    InstPair leftInstPair = visit(operation.getLeft());
    InstPair rightInstPair = visit(operation.getRight());
    LocalVar resultVar = mCurrentFunction.getTempVar(operation.getType());
    Instruction opInst = new NopInst();

    switch (operation.getOp()) {
      case ADD:
        opInst = new BinaryOperator(BinaryOperator.Op.Add, resultVar, leftInstPair.value, rightInstPair.value);
        break;
      case SUB:
        opInst = new BinaryOperator(BinaryOperator.Op.Sub, resultVar, leftInstPair.value, rightInstPair.value);
        break;
      case MULT:
        opInst = new BinaryOperator(BinaryOperator.Op.Mul, resultVar, leftInstPair.value, rightInstPair.value);
        break;
      case DIV:
        opInst = new BinaryOperator(BinaryOperator.Op.Div, resultVar, leftInstPair.value, rightInstPair.value);
        break;
      case EQ:
        opInst = new CompareInst(resultVar, CompareInst.Predicate.EQ, leftInstPair.value, rightInstPair.value);
        break;
      case NE:
        opInst = new CompareInst(resultVar, CompareInst.Predicate.NE, leftInstPair.value, rightInstPair.value);
        break;
      case GT:
        opInst = new CompareInst(resultVar, CompareInst.Predicate.GT, leftInstPair.value, rightInstPair.value);
        break;
      case GE:
        opInst = new CompareInst(resultVar, CompareInst.Predicate.GE, leftInstPair.value, rightInstPair.value);
        break;
      case LT:
        opInst = new CompareInst(resultVar, CompareInst.Predicate.LT, leftInstPair.value, rightInstPair.value);
        break;
      case LE:
        opInst = new CompareInst(resultVar, CompareInst.Predicate.LE, leftInstPair.value, rightInstPair.value);
        break;
      case LOGIC_AND:
        Instruction andEndInst = new NopInst();

        JumpInst andJumpInst = new JumpInst(leftInstPair.value);
        CopyInst andCopyInstTrue = new CopyInst(resultVar, rightInstPair.value);
        CopyInst andCopyInstFalse = new CopyInst(resultVar, BooleanConstant.get(mCurrentProgram, false));

        leftInstPair.end.setNext(0, andJumpInst);

        andJumpInst.setNext(0, andCopyInstFalse);
        andCopyInstFalse.setNext(0, andEndInst);

        andJumpInst.setNext(1, rightInstPair.start);
        rightInstPair.end.setNext(0, andCopyInstTrue);
        andCopyInstTrue.setNext(0, andEndInst);

        return new InstPair(leftInstPair.start, andEndInst, resultVar);

      case LOGIC_OR:
        Instruction endInst = new NopInst();

        JumpInst jumpInst = new JumpInst(leftInstPair.value);
        CopyInst copyInstTrue = new CopyInst(resultVar,BooleanConstant.get(mCurrentProgram,true));
        CopyInst copyInstFalse = new CopyInst(resultVar, rightInstPair.value);

        leftInstPair.end.setNext(0, jumpInst);

        jumpInst.setNext(0, rightInstPair.start);
        rightInstPair.end.setNext(0, copyInstFalse);
        copyInstFalse.setNext(0, endInst);

        jumpInst.setNext(1,copyInstTrue);
        copyInstTrue.setNext(0, endInst);
        return new InstPair(leftInstPair.start, endInst, resultVar);

      default:
        throw new IllegalStateException("Unexpected value: " + operation.getOp());
    }

    leftInstPair.end.setNext(0, rightInstPair.start);
    rightInstPair.end.setNext(0, opInst);
    return new InstPair(leftInstPair.start, opInst, resultVar);
  }

  private InstPair visit(Expression expression) {
    if (expression == null) {
      throw new NullPointerException("Expression is null");
    }

    if (expression instanceof LiteralBool) {
      return visit((LiteralBool) expression);
    } else if (expression instanceof LiteralInt) {
      return visit((LiteralInt) expression);
    } else if (expression instanceof OpExpr) {
      return visit((OpExpr) expression);
    } else if (expression instanceof VarAccess) {
      return visit((VarAccess) expression);
    } else if (expression instanceof ArrayAccess) {
      return visit((ArrayAccess) expression);
    } else if (expression instanceof Call) {
      return visit((Call) expression);
    } else {
      throw new IllegalStateException("Unexpected expression type: " + expression.getClass().getName());
    }
  }

  @Override
  public InstPair visit(ArrayAccess access) {
    Symbol baseSymbol = access.getBase();
    Type baseType = baseSymbol.getType();
    if (!(baseType instanceof ArrayType)) {
      throw new IllegalStateException("Expected ArrayType but found " + baseType.getClass().getSimpleName());
    }

    ArrayType arrayType = (ArrayType) baseType;
    Type elementType = arrayType.getBase();

    InstPair indexPair = visit(access.getIndex());
    if (indexPair == null) {
      throw new IllegalStateException("Index expression cannot be null");
    }
    LocalVar offsetVar = indexPair.value;

    AddressVar tempAddress = mCurrentFunction.getTempAddressVar(elementType);
    AddressAt addressAtInst = new AddressAt(tempAddress, baseSymbol, offsetVar);
    LoadInst loadInst = new LoadInst(mCurrentFunction.getTempVar(elementType), tempAddress);

    indexPair.end.setNext(0, addressAtInst);
    addressAtInst.setNext(0, loadInst);

    return new InstPair(indexPair.start, loadInst, loadInst.getDst());
  }

  @Override
  public InstPair visit(LiteralBool literalBool) {
    var boolConst = BooleanConstant.get(mCurrentProgram, literalBool.getValue());
    var tempVar = mCurrentFunction.getTempVar(new BoolType());
    var copyInst = new CopyInst(tempVar, boolConst);
    return new InstPair(copyInst, copyInst, tempVar);
  }

  @Override
  public InstPair visit(LiteralInt literalInt) {
    var intConst = IntegerConstant.get(mCurrentProgram, literalInt.getValue());
    var tempVar = mCurrentFunction.getTempVar(new IntType());
    var copyInst = new CopyInst(tempVar, intConst);
    return new InstPair(copyInst, copyInst, tempVar);
  }

  @Override
  public InstPair visit(Return ret) {
    InstPair returnValueInstPair = ret.getValue() != null ? visit(ret.getValue()) : null;
    ReturnInst returnInst;
    if (returnValueInstPair != null) {
      returnInst = new ReturnInst(returnValueInstPair.value);
      returnValueInstPair.end.setNext(0, returnInst);
      return new InstPair(returnValueInstPair.start, returnInst);
    } else {
      returnInst = new ReturnInst(null);
      return new InstPair(returnInst, returnInst);
    }
  }

  @Override
  public InstPair visit(Break brk) {

    return new InstPair(currentLoopEnd, new NopInst());
  }

  @Override
  public InstPair visit(Continue cont) {

    return new InstPair(currentLoopHead, new NopInst());
  }
  @Override
  public InstPair visit(IfElseBranch ifElseBranch) {
    var conditionInstPair = visit(ifElseBranch.getCondition());
    var jumpInst = new JumpInst(conditionInstPair.value);

    var thenBlockInstPair = visit(ifElseBranch.getThenBlock());
    var elseBlockInstPair = visit(ifElseBranch.getElseBlock());

    jumpInst.setNext(0, elseBlockInstPair != null ? elseBlockInstPair.start : new NopInst());
    jumpInst.setNext(1, thenBlockInstPair != null ? thenBlockInstPair.start : new NopInst());

    var endInst = new NopInst();
    if (thenBlockInstPair != null) thenBlockInstPair.end.setNext(0, endInst);
    if (elseBlockInstPair != null) elseBlockInstPair.end.setNext(0, endInst);

    conditionInstPair.end.setNext(0, jumpInst);

    return new InstPair(conditionInstPair.start, endInst);
  }

  @Override
  public InstPair visit(Loop loop) {
    Instruction previousLoopHead = currentLoopHead;
    Instruction previousLoopEnd = currentLoopEnd;

    Instruction loopHead = new NopInst();
    Instruction loopEnd = new NopInst();
    currentLoopHead = loopHead;
    currentLoopEnd = loopEnd;

    Instruction breakInst = new NopInst();
    Instruction continueInst = new NopInst();

    InstPair bodyInstPair = visit(loop.getBody());

    bodyInstPair.end.setNext(0, continueInst);
    continueInst.setNext(0, loopHead);

    LocalVar conditionVar = mCurrentFunction.getTempVar(new BoolType());
    JumpInst jumpInst = new JumpInst(conditionVar);
    loopHead.setNext(0, bodyInstPair.start);
    loopHead.setNext(1, breakInst);

    breakInst.setNext(0, loopEnd);

    currentLoopHead = previousLoopHead;
    currentLoopEnd = previousLoopEnd;

    return new InstPair(loopHead, loopEnd);
  }

  private Instruction currentLoopHead = null;
  private Instruction currentLoopEnd = null;


}
