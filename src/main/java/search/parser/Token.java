package search.parser;
import search.operators.Operator;

import java.lang.instrument.UnmodifiableModuleException;

public final class Token {

  public Token(int startPosition, String value, TokenType type, Operator operatorDefinition) {
    this.startPosition = startPosition;
    this.value = value;
    this.type = type;
    this.operatorDefinition = operatorDefinition;
  }

  public int getStartPosition() {
    return this.startPosition;
  }

  public String getValue() {
    return this.value;
  }
  public void setValue(String value) {
    if (type == TokenType.VARIABLE) {
        this.value = value;
        if (isNumeric(value)) {
          this.type = TokenType.NUMBER_LITERAL;
        } else {
          this.type = TokenType.STRING_LITERAL;
        }
    } else {
      throw new UnmodifiableModuleException("Переприсваивать значения токенов можно только токенам типа переменной");
    }
  }
  private boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch(NumberFormatException e){
      return false;
    }
  }

  public TokenType getType() {
    return this.type;
  }

  public Operator getOperatorDefinition() {
    return this.operatorDefinition;
  }

  public String toString() {
    return "Token(startPosition=" + this.getStartPosition() + ", value=" + this.getValue() + ", type=" + this.getType() + ", operatorDefinition=" + this.getOperatorDefinition() + ")";
  }

  public enum TokenType {
    BRACE_OPEN,
    BRACE_CLOSE,
    STRING_LITERAL,
    NUMBER_LITERAL,
    VARIABLE,
    INFIX_OPERATOR,
    STRUCTURE_SEPARATOR
  }

  private final int startPosition;

  private String value;

  private TokenType type;

  private final Operator operatorDefinition;

  public Token(int startPosition, String value, TokenType type) {
    this(startPosition, value, type, null);
  }


}
