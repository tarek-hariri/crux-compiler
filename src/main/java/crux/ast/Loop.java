package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for Loop.
 */
public final class Loop extends BaseNode implements Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final StatementList body;

  /**
   * @param position is the position of the loop in the source code.
   * @param body is the StatementList for the body of the loop.
   */

  public Loop(Position position, StatementList body) {
    super(position);
    this.body = body;
  }

  public StatementList getBody() {
    return body;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(body);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
