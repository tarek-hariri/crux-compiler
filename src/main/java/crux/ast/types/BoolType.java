package crux.ast.types;

/**
 * Types for Booleans values This should implement the equivalent methods along with and,or, and not
 * equivalent will check if the param is instance of BoolType
 */
public final class BoolType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  @Override
  public String toString() {
    return "bool";
  }

  @Override
  public Type and(Type that) {
    if (this.equivalent(that)) {
      return new BoolType();
    }
    return super.and(that);
  }

  @Override
  public Type or(Type that) {
    if (this.equivalent(that)) {
      return new BoolType();
    }
    return super.or(that);
  }

  @Override
  public Type not() {
    // Since not operation only applies to BoolType itself and no other type, there's no need to check equivalence.
    return new BoolType();
  }

  @Override
  public boolean equivalent(Type that) {
    return that instanceof BoolType;
  }
}

