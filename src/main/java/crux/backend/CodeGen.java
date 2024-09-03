package crux.backend;

import crux.ast.SymbolTable.Symbol;

import crux.ast.types.ArrayType;
import crux.ir.*;
import crux.ir.insts.*;

import java.util.*;

/**
 * Convert the CFG into Assembly Instructions
 */
public final class CodeGen extends InstVisitor {
  private final Program p;
  private final CodePrinter out;
  private HashMap<Variable, Integer> varIndexMap = new HashMap<>();
  private int varIndex = 1;
  private HashMap<Instruction, String> labels = new HashMap<>();

  public CodeGen(Program p) {
    this.p = p;
    // Do not change the file name that is outputted or it will break the grader!
    out = new CodePrinter("a.s");
  }

  /**
   * It should allocate space for globals call genCode for each Function
   */
  public void genCode() {
    for (Iterator<GlobalDecl> glob_it = p.getGlobals(); glob_it.hasNext();) {
      GlobalDecl g = glob_it.next();
      String name = g.getSymbol().getName();
      long size = 8;

      if(g.getSymbol().getType() instanceof ArrayType){
        size = ((ArrayType) g.getSymbol().getType()).getExtent() * 8;
      }

      out.printCode(".comm " + name + ", " + size + ", 8");
    }

    int count[] = new int[1];
    for (Iterator<Function> func_it = p.getFunctions(); func_it.hasNext();) {
      Function f = func_it.next();
      genCode(f, count);
    }
    out.printLabel("END:");

    out.close();
  }

  private String getSymbol(Variable var){
    if(varIndexMap.putIfAbsent(var, varIndex++) != null){
      varIndex--;
    }
    int offset = varIndexMap.get(var);
    return offset * -8 + "(%rbp)";
  }

  private void genCode(Function f, int[] count) {
    varIndex = 1;

    Stack<Instruction> stack = new Stack<>();
    Set<Instruction> visited = new HashSet<>();

    labels = f.assignLabels(count);
    out.printCode(".globl " + f.getName());
    out.printLabel(f.getName() + ":");

    int numSlots = f.getNumTempVars() + f.getNumTempAddressVars();
    numSlots = (numSlots % 2 == 0) ? numSlots : numSlots + 1;
    out.printCode("enter $(8 * " + numSlots + "), $0");

    List<LocalVar> parameters = f.getArguments();
    for (int i = 0; i < parameters.size(); i++) {
        LocalVar localVar = parameters.get(i);
        if (i < 6) {
            String reg = getRegisterForArg(i);
            out.printCode("movq " + reg + ", " + getSymbol(localVar));
        } else {
            String stackOffset = getRegisterForArg(i);
            out.printCode("movq " + stackOffset + ", %r10");
            out.printCode("movq %r10, " + getSymbol(localVar));
        }
    }

    stack.push(f.getStart());
    Set<String> printedLabels = new HashSet<>();
    while (!stack.isEmpty()) {
        Instruction inst = stack.pop();

        String label = labels.get(inst);
        if (label != null && printedLabels.contains(label) && visited.contains(inst)) {
            out.printCode("jmp " + labels.get(inst));
            continue;
        }

        visited.add(inst);

        if (label != null && !printedLabels.contains(label)) {
            out.printLabel(label + ":");
            printedLabels.add(label);
        }

        inst.accept(this);

        if (inst.getNext(1) != null) {
            stack.push(inst.getNext(1));
        }
        if (inst.getNext(0) != null) {
            stack.push(inst.getNext(0));
        } else {
            if (label != null && label.equals("END")) {
                out.printCode("jmp " + label);
            } else {
                out.printCode("leave");
                out.printCode("ret");
            }
        }
    }
  }


  private String getRegisterForArg(int index) {
    switch (index) {
      case 0: return "%rdi";
      case 1: return "%rsi";
      case 2: return "%rdx";
      case 3: return "%rcx";
      case 4: return "%r8";
      case 5: return "%r9";
      default: 
        int offset = 40 + (index - 6) * 8;
        return offset + "(%rbp)";  // More than 6 arguments, passed on the stack
    }
  }

  public void visit(AddressAt i) {
    out.printCode("/* AddressAt */");
    out.printCode("movq " + i.getBase().getName() + "@GOTPCREL(%rip), %r11");
    if (i.getOffset() == null) {
      out.printCode("movq %r11, " + getSymbol(i.getDst()));
    } else {
      String offsetSymbol = getSymbol(i.getOffset());
      out.printCode("movq " + offsetSymbol + ", %r10");
      out.printCode("imulq $8, %r10");
      out.printCode("addq %r10, %r11");
      out.printCode("movq %r11, " + getSymbol(i.getDst()));
    }
  }

  public void visit(BinaryOperator i) {
    out.printCode("/* BinaryOperator */");

    switch (i.getOperator()) {
      case Add:
        out.printCode("movq " + getSymbol(i.getLeftOperand()) + ", %r10");
        out.printCode("addq " + getSymbol(i.getRightOperand()) + ", %r10");
        out.printCode("movq %r10, " + getSymbol(i.getDst()));
        break;
      case Sub:
        out.printCode("movq " + getSymbol(i.getLeftOperand()) + ", %r10");
        out.printCode("subq " + getSymbol(i.getRightOperand()) + ", %r10");
        out.printCode("movq %r10, " + getSymbol(i.getDst()));
        break;
      case Mul:
        out.printCode("movq " + getSymbol(i.getLeftOperand()) + ", %r10");
        out.printCode("imulq " + getSymbol(i.getRightOperand()) + ", %r10");
        out.printCode("movq %r10, " + getSymbol(i.getDst()));
        break;
      case Div:
        out.printCode("movq " + getSymbol(i.getLeftOperand()) + ", %rax");
        out.printCode("cqto");
        out.printCode("idivq " + getSymbol(i.getRightOperand()));
        out.printCode("movq %rax, " + getSymbol(i.getDst()));
        break;
    }
  }

  public void visit(CompareInst i) {
    out.printCode("/* CompareInst */");

    out.printCode("movq $0, %rax");
    out.printCode("movq $1, %r10");
    out.printCode("movq " + getSymbol(i.getLeftOperand()) + ", %r11");
    out.printCode("movq " + getSymbol(i.getRightOperand()) + ", %r12");
    out.printCode("cmpq %r12, %r11");

    switch (i.getPredicate()) {
      case EQ:
        out.printCode("cmove %r10, %rax");
        break;
      case NE:
        out.printCode("cmovne %r10, %rax");
        break;
      case GT:
        out.printCode("cmovg %r10, %rax");
        break;
      case GE:
        out.printCode("cmovge %r10, %rax");
        break;
      case LT:
        out.printCode("cmovl %r10, %rax");
        break;
      case LE:
        out.printCode("cmovle %r10, %rax");
        break;
    }
    out.printCode("movq %rax, " + getSymbol(i.getDst()));
  }

  public void visit(CopyInst i) {
    out.printCode("/* CopyInst */");

    Value src = i.getSrcValue();
    Variable dst = i.getDstVar();

    if (src instanceof IntegerConstant) {
      long value = ((IntegerConstant) src).getValue();
      out.printCode("movq $" + value + ", %r10");
    } else if (src instanceof BooleanConstant) {
      boolean value = ((BooleanConstant) src).getValue();
      out.printCode("movq $" + (value ? 1 : 0) + ", %r10");
    } else if (src instanceof Variable) {
      out.printCode("movq " + getSymbol((Variable) src) + ", %r10");
    } else {
      throw new RuntimeException("Unsupported src type in CopyInst: " + src.getClass().getName());
    }

    out.printCode("movq %r10, " + getSymbol(dst));
  }

  public void visit(JumpInst i) {
    out.printCode("/* JumpInst */");

    String trueLabel = getUniqueLabel("L");
    String falseLabel = getUniqueLabel("L");

    if (i.getPredicate() != null) {
        if (!labels.containsKey(i.getNext(1))) {
            labels.put(i.getNext(1), trueLabel);
        }
        if (!labels.containsKey(i.getNext(0))) {
            labels.put(i.getNext(0), falseLabel);
        }

        out.printCode("cmpq $1, " + getSymbol(i.getPredicate()));
        out.printCode("je " + labels.get(i.getNext(1)));
        out.printCode("jmp " + labels.get(i.getNext(0)));
    } else {
        if (!labels.containsKey(i.getNext(0))) {
            labels.put(i.getNext(0), falseLabel);
        }
        out.printCode("jmp " + labels.get(i.getNext(0)));
    }
  }

  private String getUniqueLabel(String base) {
      return base + "_" + (labels.size() + 1);
  }



  public void visit(LoadInst i) {
    out.printCode("/* LoadInst */");

    out.printCode("movq " + getSymbol(i.getSrcAddress()) + ", %r10");
    out.printCode("movq (%r10), %r11");
    out.printCode("movq %r11, " + getSymbol(i.getDst()));
  }

  public void visit(NopInst i) {
  }

  public void visit(StoreInst i) {
    out.printCode("/* StoreInst */");

    out.printCode("movq " + getSymbol(i.getDestAddress()) + ", %r10");
    Value src = i.getSrcValue();
    if (src instanceof IntegerConstant) {
      long value = ((IntegerConstant) src).getValue();
      out.printCode("movq $" + value + ", %r11");
    } else {
      out.printCode("movq " + getSymbol((Variable) src) + ", %r11");
    }
    out.printCode("movq %r11, (%r10)");
  }

  public void visit(ReturnInst i) {
    out.printCode("/* ReturnInst */");

    if (i.getReturnValue() != null) {
      out.printCode("movq " + getSymbol(i.getReturnValue()) + ", %rax");
    }
    out.printCode("leave");
    out.printCode("ret");
  }

  public void visit(CallInst i) {
    out.printCode("/* CallInst */");

    List<LocalVar> parameters = i.getParams();
    for (int j = 0; j < parameters.size() && j < 6; j++) {
        String reg = getRegisterForArg(j);
        LocalVar localVar = parameters.get(j);
        out.printCode("movq " + getSymbol(localVar) + ", " + reg);
    }

    int numParamsOnStack = Math.max(0, parameters.size() - 6);
    for (int j = parameters.size() - 1; j >= 6; j--) {
        out.printCode("movq " + getSymbol(parameters.get(j)) + ", %r10");
        out.printCode("subq $8, %rsp");
        out.printCode("movq %r10, 0(%rsp)");
    }

    if (numParamsOnStack > 0) {
        out.printCode("subq $" + (numParamsOnStack * 8) + ", %rsp");
    }

    out.printCode("call " + i.getCallee().getName());

    if (numParamsOnStack > 0) {
        out.printCode("addq $" + (numParamsOnStack * 8) + ", %rsp");
    }

    if (i.getDst() != null) {
        out.printCode("movq %rax, " + getSymbol(i.getDst()));
    }
}

  public void visit(UnaryNotInst i) {
    out.printCode("/* UnaryNotInst */");

    out.printCode("movq $1, %r11");
    out.printCode("subq " + getSymbol(i.getInner()) + ", %r11");
    out.printCode("movq %r11, " + getSymbol(i.getDst()));
  }
}
