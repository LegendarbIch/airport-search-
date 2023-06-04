package search.config;

import search.operators.Operator;

import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.stream;


public class MapBasedOperatorDictionary implements OperatorDictionary {
  final Map<String, Operator> infixOperators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  public static OperatorDictionary ofOperators(Map.Entry<String, Operator>... operators) {
    OperatorDictionary dictionary = new MapBasedOperatorDictionary();
    stream(operators).forEach(entry -> dictionary.addOperator(entry.getKey(), entry.getValue()));
    return dictionary;
  }

  @Override
  public void addOperator(String operatorString, Operator operator) {
      infixOperators.put(operatorString, operator);
  }

  @Override
  public Operator getInfixOperator(String operatorString) {
    return infixOperators.get(operatorString);
  }
}
