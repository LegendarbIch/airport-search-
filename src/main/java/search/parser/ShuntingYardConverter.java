package search.parser;


import search.config.ExpressionConfiguration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import search.operators.Operator;
import search.parser.Token.TokenType;

import static search.parser.Token.TokenType.STRUCTURE_SEPARATOR;


public class ShuntingYardConverter {

  private final List<Token> expressionTokens;

  private final String originalExpression;

  private final ExpressionConfiguration configuration;

  private final Deque<Token> operatorStack = new ArrayDeque<>();
  private final Deque<AbstractSyntaxTreeNode> operandStack = new ArrayDeque<>();

  public ShuntingYardConverter(
      String originalExpression,
      List<Token> expressionTokens,
      ExpressionConfiguration configuration) {
    this.originalExpression = originalExpression;
    this.expressionTokens = expressionTokens;
    this.configuration = configuration;
  }

  public AbstractSyntaxTreeNode toAbstractSyntaxTree() throws ParseException {
    for (Token currentToken : expressionTokens) {
      switch (currentToken.getType()) {
        case VARIABLE:
        case NUMBER_LITERAL:
        case STRING_LITERAL:
          operandStack.push(new AbstractSyntaxTreeNode(currentToken));
          break;
        case INFIX_OPERATOR:
          processOperator(currentToken);
          break;
        case BRACE_OPEN:
          processBraceOpen(currentToken);
          break;
        case BRACE_CLOSE:
          processBraceClose();
          break;
        case STRUCTURE_SEPARATOR:
          processStructureSeparator(currentToken);
          break;
        default:
          throw new ParseException(
              currentToken, "Неизвестный тип токена " + currentToken.getType());
      }
    }

    while (!operatorStack.isEmpty()) {
      Token token = operatorStack.pop();
      createOperatorNode(token);
    }

    if (operandStack.isEmpty()) {
      throw new ParseException(this.originalExpression, "Пустое выражение");
    }

    if (operandStack.size() > 1) {
      throw new ParseException(this.originalExpression, "Много операндов");
    }

    return operandStack.pop();
  }

  private void processStructureSeparator(Token currentToken) throws ParseException {
    Token nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
    while (nextToken != null && nextToken.getType() == STRUCTURE_SEPARATOR) {
      Token token = operatorStack.pop();
      createOperatorNode(token);
      nextToken = operatorStack.peek();
    }
    operatorStack.push(currentToken);
  }

  private void processBraceOpen( Token currentToken) {
    operatorStack.push(currentToken);
  }

  private void processBraceClose() throws ParseException {
    processOperatorsFromStackUntilTokenType();
    operatorStack.pop();
  }


  private void processOperatorsFromStackUntilTokenType()
      throws ParseException {
    while (!operatorStack.isEmpty() && operatorStack.peek().getType() != TokenType.BRACE_OPEN) {
      Token token = operatorStack.pop();
      createOperatorNode(token);
    }
  }

  private void createOperatorNode(Token token) throws ParseException {
    if (operandStack.isEmpty()) {
      throw new ParseException(token, "Отсутствует операнд для оператора");
    }

    AbstractSyntaxTreeNode operand1 = operandStack.pop();

      if (operandStack.isEmpty()) {
        throw new ParseException(token, "Отсутствует второй операнд для оператора");
      }
      AbstractSyntaxTreeNode operand2 = operandStack.pop();
      operandStack.push(new AbstractSyntaxTreeNode(token, operand2, operand1));
  }

  private void processOperator(Token currentToken) throws ParseException {
    Token nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
    while (isOperator(nextToken)
        && isNextOperatorOfHigherPrecedence(
            currentToken.getOperatorDefinition(), nextToken.getOperatorDefinition())) {
      Token token = operatorStack.pop();
      createOperatorNode(token);
      nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
    }
    operatorStack.push(currentToken);
  }

  private boolean isNextOperatorOfHigherPrecedence(
          Operator currentOperator, Operator nextOperator) {
    // null всегда имеет более высокий приоритет, чем другие операторы
    if (nextOperator == null) {
      return true;
    }

    if (currentOperator.isLeftAssociative()) {
      return currentOperator.getPrecedence(configuration)
          <= nextOperator.getPrecedence(configuration);
    } else {
      return currentOperator.getPrecedence(configuration)
          < nextOperator.getPrecedence(configuration);
    }
  }

  private boolean isOperator(Token token) {
    if (token == null) {
      return false;
    }
    TokenType tokenType = token.getType();
    switch (tokenType) {
      case INFIX_OPERATOR:
      case STRUCTURE_SEPARATOR:
        return true;
      default:
        return false;
    }
  }
}
