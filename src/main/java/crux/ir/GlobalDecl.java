package crux.ir;

import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import crux.ast.SymbolTable.Symbol;

/**
 * Allocates a chunk of memory, either a global variable or a global array.
 * <p>
 * Operation (pseudo-code):
 *
 * <pre>
 * {@code
 * if (global)
 *     destVar = allocateInDataSection(numElement)
 * else
 *     destVar = reserveStackMemory(numElement)
 * }
 * </pre>
 */
public final class GlobalDecl implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  Symbol mSymbol;
  IntegerConstant mNumElement;
  List<Instruction> initInstructions;

  public GlobalDecl(Symbol symbol, IntegerConstant numElement) {
    mSymbol = symbol;
    mNumElement = numElement;
    initInstructions = new Vector<>();
  }

  public Symbol getSymbol() {
    return mSymbol;
  }

  public IntegerConstant getNumElement() {
    return mNumElement;
  }

  public void addInitInstruction(Instruction inst) {
    initInstructions.add(inst);
  }

  public List<Instruction> getInitInstructions() {
    return initInstructions;
  }

  public String format(Function<Value, String> valueFormatter) {
    var destVar = mSymbol.getName();
    var typeStr = getSymbol().getType().toString();
    var numElement = valueFormatter.apply(getNumElement());
    return String.format("%s = allocate %s, %s", destVar, typeStr, numElement);
  }
}
