package search.parser;

import lombok.ToString;
import search.exceptions.BaseException;

@ToString(callSuper = true)
public class ParseException extends BaseException {

  public ParseException(int startPosition, int endPosition, String tokenString, String message) {
    super(startPosition, endPosition, tokenString, message);
  }

  public ParseException(String expression, String message) {
    super(1, expression.length(), expression, message);
  }

  public ParseException(Token token, String message) {
    super(
        token.getStartPosition(),
        token.getStartPosition() + token.getValue().length() - 1,
        token.getValue(),
        message);
  }
}
