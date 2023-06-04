package search.operators;

public class OperatorAnnotationNotFoundException extends RuntimeException {

  public OperatorAnnotationNotFoundException(String className) {
    super("Аннотация оператора для '" + className + "' не найдена");
  }
}
