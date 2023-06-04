package search.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
public class BaseException extends Exception {

  @Getter private final int startPosition;
  @Getter private final int endPosition;
  @Getter private final String tokenString;
  @Getter private final String message;

  public BaseException(int startPosition, int endPosition, String tokenString, String message) {
    super(message);
    this.startPosition = startPosition;
    this.endPosition = endPosition;
    this.tokenString = tokenString;
    this.message = super.getMessage();
  }
}
