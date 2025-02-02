package crux.ir.insts;

import crux.ir.Instruction;
import crux.ir.Value;
import crux.ir.LocalVar;

import java.util.List;
import java.util.function.Function;

/**
 * Any binary expression operator.
 */
public final class BinaryOperator extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public enum Op {
    Add, Sub, Mul, Div, And, Or
  }

  protected Op mOp;

  public Op getOperator() {
    return mOp;
  }

  public BinaryOperator(Op op, LocalVar destVar, LocalVar lhsValue, LocalVar rhsValue) {
    super(destVar, List.of(lhsValue, rhsValue));
    mOp = op;
  }

  public LocalVar getLeftOperand() {
    return (LocalVar) mOperands.get(0);
  }

  public LocalVar getRightOperand() {
    return (LocalVar) mOperands.get(1);
  }

  public LocalVar getDst() {
    return (LocalVar) mDestVar;
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    String opStr = "";
    switch (getOperator()) {
      case Add:
        opStr = "+";
        break;
      case Sub:
        opStr = "-";
        break;
      case Mul:
        opStr = "*";
        break;
      case Div:
        opStr = "/";
        break;
      case And:
        opStr = "&&";
        break;
      case Or:
        opStr = "||";
        break;
    }
    var destVar = valueFormatter.apply(mDestVar);
    var lhs = valueFormatter.apply(mOperands.get(0));
    var rhs = valueFormatter.apply(mOperands.get(1));
    return String.format("%s = %s %s %s", destVar, lhs, opStr, rhs);
  }
}
