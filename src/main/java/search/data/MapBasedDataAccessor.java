package search.data;

import java.util.Map;
import java.util.TreeMap;

public class MapBasedDataAccessor implements DataAccessor {

  private final Map<String, EvaluationValue> variables =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  @Override
  public EvaluationValue getData(String variable) {
    return variables.get(variable);
  }

  @Override
  public void setData(String variable, EvaluationValue value) {
    variables.put(variable, value);
  }
}
