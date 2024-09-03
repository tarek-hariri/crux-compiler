package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for Continue statement that can appear in a loop.
 */
public final class Continue extends BaseNode implements Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public Continue(Position position) {
    super(position);
  }

  @Override
  public List<Node> getChildren() {
    return List.of();
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
