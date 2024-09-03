package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST Node for DeclarationList.
 */
public final class DeclarationList extends ListNode<Declaration> implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private List<Declaration> declarations;

  public DeclarationList(Position position, List<Declaration> declarations) {

    super(position, declarations);
    this.declarations = declarations;
  }

  public List<Declaration> getDeclarations() {
    return declarations;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
