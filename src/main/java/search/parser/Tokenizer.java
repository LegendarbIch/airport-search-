package search.parser;

import search.config.ExpressionConfiguration;
import search.config.OperatorDictionary;

import java.util.ArrayList;
import java.util.List;
import static search.parser.Token.TokenType.*;

import search.operators.Operator;
import search.parser.Token.TokenType;

public class Tokenizer {

  private final String expressionString;

  private final OperatorDictionary operatorDictionary;

  private final List<Token> tokens = new ArrayList<>();

  private int currentColumnIndex = 0;

  private int currentChar = -2;

  private int braceBalance;


  public Tokenizer(String expressionString, ExpressionConfiguration configuration) {
    this.expressionString = expressionString;
    this.operatorDictionary = configuration.getOperatorDictionary();
  }
  public List<Token> parse() throws ParseException {
    Token currentToken = getNextToken();
    while (currentToken != null) {
      validateToken(currentToken);
      tokens.add(currentToken);
      currentToken = getNextToken();
    }

    if (braceBalance > 0) {
      throw new ParseException(expressionString, "Закрывающие скобки не найдены");
    }

    return tokens;
  }


  private void validateToken(Token currentToken) throws ParseException {
    Token previousToken = getPreviousToken();
    if (previousToken != null
        && previousToken.getType() == INFIX_OPERATOR
        && invalidTokenAfterInfixOperator(currentToken)) {
      throw new ParseException(currentToken, "Неизвестный токен после инфиксного оператора");
    }
  }

  private boolean invalidTokenAfterInfixOperator(Token token) {
    switch (token.getType()) {
      case INFIX_OPERATOR:
      case BRACE_CLOSE:
      default:
        return false;
    }
  }

  private Token getNextToken() throws ParseException {

    skipBlanks();

    if (currentChar == -1) {
      return null;
    }

    //идентифицируем и парсим токен
    if (currentChar == '\'' || currentChar == '\"') {
      return parseStringLiteral();
    } else if (currentChar == '(') {
      return parseBraceOpen();
    } else if (currentChar == ')') {
      return parseBraceClose();
    } else if (currentChar == '.'
        && !isNextCharNumberChar()) {
      return parseStructureSeparator();
    } else if (isAtIdentifierStart()) {
      return parseIdentifier();
    } else if (isAtNumberStart()) {
      return parseNumberLiteral();
    } else {
      return parseOperator();
    }
  }

  private Token parseStructureSeparator() throws ParseException {
    Token token = new Token(currentColumnIndex, ".", TokenType.STRUCTURE_SEPARATOR);
    if (structureSeparatorNotAllowed()) {
      throw new ParseException(token, "Здесь не позволено использовать структурный разделитель");
    }
    consumeChar();
    return token;
  }

  private Token parseBraceClose() throws ParseException {
    Token token = new Token(currentColumnIndex, ")", TokenType.BRACE_CLOSE);
    consumeChar();
    braceBalance--;
    if (braceBalance < 0) {
      throw new ParseException(token, "Неожиданная закрывающая скобка");
    }
    return token;
  }

  private Token parseBraceOpen() {
    Token token = new Token(currentColumnIndex, "(", BRACE_OPEN);
    consumeChar();
    braceBalance++;
    return token;
  }

  private Token getPreviousToken() {
    return tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
  }

  private Token parseOperator() throws ParseException {
    int tokenStartIndex = currentColumnIndex;
    StringBuilder tokenValue = new StringBuilder();
    while (true) {
      tokenValue.append((char) currentChar);
      String tokenString = tokenValue.toString();
      String possibleNextOperator = tokenString + (char) peekNextChar();
      boolean possibleNextOperatorFound =
          (infixOperatorAllowed() && operatorDictionary.hasInfixOperator(possibleNextOperator));
      consumeChar();
      if (!possibleNextOperatorFound) {
        break;
      }
    }
    String tokenString = tokenValue.toString();
    if (operatorDictionary.hasInfixOperator(tokenString)) {
      Operator operator = operatorDictionary.getInfixOperator(tokenString);
      return new Token(tokenStartIndex, tokenString, TokenType.INFIX_OPERATOR, operator);
    } else if (tokenString.equals(".")) {
      return new Token(tokenStartIndex, tokenString, STRUCTURE_SEPARATOR);
    }
    throw new ParseException(
        tokenStartIndex,
        tokenStartIndex + tokenString.length() - 1,
        tokenString,
        "Неизвестный оператор " + tokenString);
  }

  private boolean structureSeparatorNotAllowed() {
    Token previousToken = getPreviousToken();

    if (previousToken == null) {
      return true;
    }

    switch (previousToken.getType()) {
      case BRACE_CLOSE:
      case VARIABLE:
      case STRING_LITERAL:
        return false;
      default:
        return true;
    }
  }


  private boolean infixOperatorAllowed() {
    Token previousToken = getPreviousToken();

    if (previousToken == null) {
      return false;
    }

    switch (previousToken.getType()) {
      case BRACE_CLOSE:
      case VARIABLE:
      case STRING_LITERAL:
      case NUMBER_LITERAL:
        return true;
      default:
        return false;
    }
  }

  private Token parseNumberLiteral()  {
    int tokenStartIndex = currentColumnIndex;
    StringBuilder tokenValue = new StringBuilder();

      while (currentChar != -1 && isAtNumberChar()) {
        tokenValue.append((char) currentChar);
        consumeChar();
      }
    return new Token(tokenStartIndex, tokenValue.toString(), TokenType.NUMBER_LITERAL);
  }

  private Token parseIdentifier()  {
    int tokenStartIndex = currentColumnIndex;
    StringBuilder tokenValue = new StringBuilder();
    while (currentChar != -1 && isAtIdentifierChar()) {
      tokenValue.append((char) currentChar);
      consumeChar();
    }
    String tokenName = tokenValue.toString();

    if (operatorDictionary.hasInfixOperator(tokenName)) {
      return new Token(
          tokenStartIndex,
          tokenName,
          TokenType.INFIX_OPERATOR,
          operatorDictionary.getInfixOperator(tokenName));
    }

    skipBlanks();
    return new Token(tokenStartIndex, tokenName, TokenType.VARIABLE);
  }

  Token parseStringLiteral() throws ParseException {
    int tokenStartIndex = currentColumnIndex;
    StringBuilder tokenValue = new StringBuilder();
    consumeChar();
    boolean inQuote = true;
    while (inQuote && currentChar != -1) {
      if (currentChar == '\\') {
        consumeChar();
        tokenValue.append(escapeCharacter(currentChar));
      }
      if (currentChar == '\"') {
        inQuote = false;
      } else {
        tokenValue.append((char) currentChar);
      }
      consumeChar();
    }
    if (inQuote) {
      throw new ParseException(
          tokenStartIndex, currentColumnIndex, tokenValue.toString(), "Закрывающая скобка не найдена");
    }
    return new Token(tokenStartIndex, tokenValue.toString(), TokenType.STRING_LITERAL);
  }

  private char escapeCharacter(int character) throws ParseException {
    switch (character) {
      case '\'':
        return '\'';
      case '"':
        return '"';
      case '\\':
        return '\\';
      case 'n':
        return '\n';
      case 'N':
        return 'n';
      default:
        throw new ParseException(
            currentColumnIndex, 1, "\\" + (char) character, "Неизвестный экранирующий символ");
    }
  }

  private boolean isAtNumberStart() {
    if (currentChar == '-' && Character.isDigit(peekNextChar())) {
      return true;
    }
    if (Character.isDigit(currentChar)) {
      return true;
    }
    return currentChar == '.' && Character.isDigit(peekNextChar());
  }

  private boolean isAtNumberChar() {
    int previousChar = peekPreviousChar();

    if (previousChar == '.') {
      return Character.isDigit(currentChar);
    }

    return Character.isDigit(currentChar)
        || currentChar == '.' || currentChar == '-';
  }

  private boolean isNextCharNumberChar() {
    if (peekNextChar() == -1) {
      return false;
    }
    consumeChar();
    boolean isAtNumber = isAtNumberChar();
    currentColumnIndex--;
    currentChar = expressionString.charAt(currentColumnIndex - 1);
    return isAtNumber;
  }


  private boolean isAtIdentifierStart() {
    return Character.isLetter(currentChar);
  }

  private boolean isAtIdentifierChar() {
    return Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '[' || currentChar == ']' || currentChar == '_';
  }

  private void skipBlanks() {
    if (currentChar == -2) {
      consumeChar();
    }
    while (currentChar != -1 && Character.isWhitespace(currentChar)) {
      consumeChar();
    }
  }

  private int peekNextChar() {
    return currentColumnIndex == expressionString.length()
        ? -1
        : expressionString.charAt(currentColumnIndex);
  }

  private int peekPreviousChar() {
    return currentColumnIndex == 1 ? -1 : expressionString.charAt(currentColumnIndex - 2);
  }

  private void consumeChar() {
    if (currentColumnIndex == expressionString.length()) {
      currentChar = -1;
    } else {
      currentChar = expressionString.charAt(currentColumnIndex++);
    }
  }
}
