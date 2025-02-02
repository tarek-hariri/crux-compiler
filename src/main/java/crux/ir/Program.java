package crux.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import crux.ast.types.Type;

/**
 * A program consists of a collection of functions and a collection of global variables.
 */
public final class Program implements Formattable, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private List<Function> mFunctions;
  private List<GlobalDecl> mGlobalVars;
  private int mTempAddressVarCounter = 0;

  private static final int PROGRAM_FORMAT_INDENT = 2;

  public Program() {
    mFunctions = new ArrayList<>();
    mGlobalVars = new ArrayList<>();
  }

  public AddressVar getTempAddressVar(Type type) {
    String name = String.format("av%d", mTempAddressVarCounter++);
    return new AddressVar(type, name);
  }

  public void addGlobalVar(GlobalDecl globalAllocate) {
    mGlobalVars.add(globalAllocate);
  }

  public Iterator<GlobalDecl> getGlobals() {
    return mGlobalVars.iterator();
  }

  public void addFunction(Function function) {
    mFunctions.add(function);
  }

  public Iterator<Function> getFunctions() {
    return mFunctions.iterator();
  }

  @Override
  public String format(java.util.function.Function<Value, String> valueFormatter) {
    var builder = new StringBuilder();
    builder.append("digraph Program {\n");
    // Styles
    builder.append(" ".repeat(PROGRAM_FORMAT_INDENT)).append("node [shape=rectangle]; \n");

    // Print global vars first
    if (mGlobalVars.size() > 0) {
      int indent = PROGRAM_FORMAT_INDENT;
      builder.append(" ".repeat(indent)).append("subgraph cluster_global_var {\n");
      indent *= 2;
      builder.append(" ".repeat(indent)).append("color=grey;").append("\n")
          .append(" ".repeat(indent)).append("label = \"Global Variable\";\n");
      List<String> globalVarIds = new ArrayList<>();
      // Nodes
      for (var globalVar : mGlobalVars) {
        var id = globalVar.getSymbol().getName();
        // Strip out '%' at the beginning, if there is any
        id = id.replaceAll("%", "");
        globalVarIds.add(id);
        builder.append(" ".repeat(indent)).append(id).append(" [label=\"")
            .append(globalVar.format(valueFormatter)).append("\"];\n");
      }
      // Edges
      builder.append(" ".repeat(indent)).append(String.join(" -> ", globalVarIds)).append("; \n");
      indent /= 2;
      builder.append(" ".repeat(indent)).append("}\n");
    }

    // Print functions
    for (var function : mFunctions) {
      builder.append("\n").append(function.format(valueFormatter)).append("\n");
    }

    builder.append("}");
    return builder.toString();
  }
}
