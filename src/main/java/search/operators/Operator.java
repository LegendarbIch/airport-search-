package search.operators;

import search.exceptions.EvaluationException;
import search.Expression;
import search.config.ExpressionConfiguration;
import search.data.EvaluationValue;
import search.parser.Token;

public interface Operator {

  enum OperatorType {
    //оператор по типу x>y
    INFIX_OPERATOR
  }

  // приоритет логического ИЛИ
  int OPERATOR_PRECEDENCE_OR = 2;

  // приоритет логического И
  int OPERATOR_PRECEDENCE_AND = 4;

  // приоритет сравнительных операторов: =, <>
  int OPERATOR_PRECEDENCE_EQUALITY = 7;

  //Приоритет сравнительных операторов: <, >, <=, >=
  int OPERATOR_PRECEDENCE_COMPARISON = 10;

  int getPrecedence();

  boolean isLeftAssociative();

  boolean isInfix();

  int getPrecedence(ExpressionConfiguration configuration);

  EvaluationValue evaluate(Expression expression, Token operatorToken, EvaluationValue... operands)
      throws EvaluationException;
}
