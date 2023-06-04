package search.data;

public interface DataAccessor {

  EvaluationValue getData(String variable);

  void setData(String variable, EvaluationValue value);
}
