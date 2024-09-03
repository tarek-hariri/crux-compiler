package crux.ast.types;

import java.util.Iterator;


/**
 * The field args is a TypeList with a type for each param. The type ret is the type of the function
 * return. The function return could be int, bool, or void. This class should implement the call
 * method.
 */
public final class FuncType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  private TypeList args;
  private Type ret;

  public FuncType(TypeList args, Type returnType) {
    this.args = args;
    this.ret = returnType;
  }

  public Type getRet() {
    return ret;
  }

  public TypeList getArgs() {
    return args;
  }

  @Override
  public String toString() {
    return "func(" + args + "):" + ret;
  }

  public Type call(TypeList argTypes) {
    if(args == null && argTypes.size() == 0){
      return ret;
    }
    if (args.size() != argTypes.size()) {
      return new ErrorType("Incorrect number of arguments");
    }

    Iterator<Type> expectedArgs = args.iterator();
    Iterator<Type> providedArgs = argTypes.iterator();

    while (expectedArgs.hasNext() && providedArgs.hasNext()) {
      Type expected = expectedArgs.next();
      Type provided = providedArgs.next();
      if (!expected.equivalent(provided)) {
        return new ErrorType("Argument type mismatch: expected " + expected + ", provided " + provided);
      }
    }
    return ret;
  }

  @Override
  public boolean equivalent(Type that) {
    if (that instanceof FuncType) {
      FuncType other = (FuncType) that;
      return this.args.equivalent(other.args) && this.ret.equivalent(other.ret);
    }
    return false;
  }
}

