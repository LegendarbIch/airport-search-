package search.parser;


import java.util.Arrays;
import java.util.List;

public final class AbstractSyntaxTreeNode {

  private final List<AbstractSyntaxTreeNode> parameters;

  private final Token token;

  public AbstractSyntaxTreeNode(Token token, AbstractSyntaxTreeNode... parameters) {
    this.token = token;
    this.parameters = Arrays.asList(parameters);
  }

  public List<AbstractSyntaxTreeNode> getParameters() {
    return this.parameters;
  }

  public Token getToken() {
    return this.token;
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof AbstractSyntaxTreeNode)) return false;
    final AbstractSyntaxTreeNode other = (AbstractSyntaxTreeNode) o;
    final Object this$parameters = this.getParameters();
    final Object other$parameters = other.getParameters();
    if (this$parameters == null ? other$parameters != null : !this$parameters.equals(other$parameters)) return false;
    final Object this$token = this.getToken();
    final Object other$token = other.getToken();
    if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $parameters = this.getParameters();
    result = result * PRIME + ($parameters == null ? 43 : $parameters.hashCode());
    final Object $token = this.getToken();
    result = result * PRIME + ($token == null ? 43 : $token.hashCode());
    return result;
  }

  public String toString() {
    return "AbstractSyntaxTreeNode(parameters=" + this.getParameters() + ", token=" + this.getToken() + ")";
  }
}
