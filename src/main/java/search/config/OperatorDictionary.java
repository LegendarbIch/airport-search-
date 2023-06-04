package search.config;

import search.operators.Operator;

public interface OperatorDictionary {

  void addOperator(String operatorString, Operator operator);

  default boolean hasInfixOperator(String operatorString) {
    return getInfixOperator(operatorString) != null;
  }

  Operator getInfixOperator(String operatorString);
}
