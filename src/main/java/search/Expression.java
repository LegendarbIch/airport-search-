package search;

import lombok.Getter;
import search.config.ExpressionConfiguration;
import search.data.DataAccessor;
import search.data.EvaluationValue;
import search.exceptions.EvaluationException;
import search.parser.*;

import java.util.*;

public class Expression {
  @Getter private final ExpressionConfiguration configuration;

  @Getter private final String expressionString;

  @Getter private final DataAccessor dataAccessor;

  @Getter
  private final Map<String, EvaluationValue> constants =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  private AbstractSyntaxTreeNode abstractSyntaxTree;

  public Expression(String expressionString) {
    this(expressionString, ExpressionConfiguration.defaultConfiguration());
  }

  public Expression(String expressionString, ExpressionConfiguration configuration) {
    this.expressionString = expressionString;
    this.configuration = configuration;
    this.dataAccessor = configuration.getDataAccessorSupplier().get();
  }

  public EvaluationValue evaluate() throws EvaluationException, ParseException {
    return evaluateSubtree(getAbstractSyntaxTree());
  }

  public EvaluationValue evaluateSubtree(AbstractSyntaxTreeNode startNode) throws EvaluationException {
    Token token = startNode.getToken();
    EvaluationValue result;
    switch (token.getType()) {
      case NUMBER_LITERAL:
        result = EvaluationValue.numberOfString(token.getValue());
        break;
      case STRING_LITERAL:
        result = new EvaluationValue(token.getValue());
        break;
      case VARIABLE:
        result = getVariableOrConstant(token);
        if (result.isExpressionNode()) {
          result = evaluateSubtree(result.getExpressionNode());
        }
        break;
      case INFIX_OPERATOR:
        result =
            token
                .getOperatorDefinition()
                .evaluate(
                    this,
                    token,
                    evaluateSubtree(startNode.getParameters().get(0)),
                    evaluateSubtree(startNode.getParameters().get(1)));
        break;
      default:
        throw new EvaluationException(token, "Неопределенный токен: " + token);
    }

    return result;
  }

  private EvaluationValue getVariableOrConstant(Token token) throws EvaluationException {
    EvaluationValue result = constants.get(token.getValue());
    if (result == null) {
      result = getDataAccessor().getData(token.getValue());
    }
    if (result == null) {
      throw new EvaluationException(
          token, String.format("Переменное или константное значение для '%s' не найдено", token.getValue()));
    }
    return result;
  }

  public AbstractSyntaxTreeNode getAbstractSyntaxTree() throws ParseException {
    if (abstractSyntaxTree == null) {
      Tokenizer tokenizer = new Tokenizer(expressionString, configuration);
      ShuntingYardConverter converter =
          new ShuntingYardConverter(expressionString, tokenizer.parse(), configuration);
      abstractSyntaxTree = converter.toAbstractSyntaxTree();
    }
    return abstractSyntaxTree;
  }

  public AbstractSyntaxTreeNode createExpressionNode(String expression) throws ParseException {
    Tokenizer tokenizer = new Tokenizer(expression, configuration);
    ShuntingYardConverter converter =
        new ShuntingYardConverter(expression, tokenizer.parse(), configuration);
    return converter.toAbstractSyntaxTree();
  }


  public List<AbstractSyntaxTreeNode> getAllASTNodes() throws ParseException {
    return getAllASTNodesForNode(getAbstractSyntaxTree());
  }

  private List<AbstractSyntaxTreeNode> getAllASTNodesForNode(AbstractSyntaxTreeNode node) {
    List<AbstractSyntaxTreeNode> nodes = new ArrayList<>();
    nodes.add(node);
    for (AbstractSyntaxTreeNode child : node.getParameters()) {
      nodes.addAll(getAllASTNodesForNode(child));
    }
    return nodes;
  }


}
