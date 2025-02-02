package crux.ast.types;

/**
 * This class declares methods that can be applied on crux types for example, add can be applied on
 * two Integers type.
 */
public abstract class Type {
  Type add(Type that) {

    return new ErrorType("cannot add " + this + " with " + that);
  }

  Type sub(Type that) {

    return new ErrorType("cannot subtract " + this + " from " + that);
  }

  Type mul(Type that) {

    return new ErrorType("cannot multiply " + this + " with " + that);
  }

  Type div(Type that) {

    return new ErrorType("cannot divide " + this + " by " + that);
  }

  Type and(Type that) {

    return new ErrorType("cannot compute " + this + " and " + that);
  }

  Type or(Type that) {

    return new ErrorType("cannot compute " + this + " or " + that);
  }

  Type not() {

    return new ErrorType("cannot negate " + this);
  }

  Type compare(Type that) {

    return new ErrorType("cannot compare " + this + " with " + that);
  }

  Type index(Type that) {

    return new ErrorType("cannot index " + this + " with " + that);
  }

  Type call(Type args) {

    return new ErrorType("cannot call " + this + " using " + args);
  }

  Type assign(Type source) {

    return new ErrorType("cannot assign " + source + " to " + this);
  }

  public abstract boolean equivalent(Type that);
}
