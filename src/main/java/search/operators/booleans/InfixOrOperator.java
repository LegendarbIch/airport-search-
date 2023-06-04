package search.operators.booleans;


import search.Expression;
import search.data.EvaluationValue;
import search.operators.AbstractOperator;
import search.operators.InfixOperator;
import search.parser.Token;

import static search.operators.Operator.OPERATOR_PRECEDENCE_OR;

@InfixOperator(precedence = OPERATOR_PRECEDENCE_OR)
public class InfixOrOperator extends AbstractOperator {

  @Override
  public EvaluationValue evaluate(
          Expression expression, Token operatorToken, EvaluationValue... operands) {
    return new EvaluationValue(operands[0].getBooleanValue() || operands[1].getBooleanValue());
  }
}
