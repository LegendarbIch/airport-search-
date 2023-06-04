package search.exceptions;
import search.parser.Token;
public class EvaluationException extends BaseException {

  public EvaluationException(Token token, String message) {
    super(
        token.getStartPosition(),
        token.getStartPosition() + token.getValue().length(),
        token.getValue(),
        message);
  }

  public static EvaluationException ofUnsupportedDataTypeInOperation(Token token) {
    return new EvaluationException(token, "Неподдержимаемые типы данных в процессе выполнения");
  }
}
