package search.operators;


import lombok.Getter;
import search.config.ExpressionConfiguration;


public abstract class AbstractOperator implements Operator {

  @Getter private final int precedence;

  private final boolean leftAssociative;

  OperatorType type;

  protected AbstractOperator() {
    InfixOperator infixAnnotation = getClass().getAnnotation(InfixOperator.class);
    if (infixAnnotation != null) {
      this.type = OperatorType.INFIX_OPERATOR;
      this.precedence = infixAnnotation.precedence();
      this.leftAssociative = infixAnnotation.leftAssociative();
    } else {
      throw new OperatorAnnotationNotFoundException(this.getClass().getName());
    }
  }

  @Override
  public int getPrecedence(ExpressionConfiguration configuration) {
    return getPrecedence();
  }

  @Override
  public boolean isLeftAssociative() {
    return leftAssociative;
  }

  @Override
  public boolean isInfix() {
    return type == OperatorType.INFIX_OPERATOR;
  }
}
