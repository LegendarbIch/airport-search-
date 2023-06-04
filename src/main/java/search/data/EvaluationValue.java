package search.data;

import lombok.Value;
import search.parser.AbstractSyntaxTreeNode;

import java.util.Objects;


@Value
public class EvaluationValue implements Comparable<EvaluationValue> {

  public enum DataType {
    STRING,
    NUMBER,
    BOOLEAN,
    EXPRESSION_NODE
  }

  Object value;

  DataType dataType;

  public EvaluationValue(Object value) {
    if (value instanceof Double) {
      this.dataType = DataType.NUMBER;
      this.value = value;
    } else if (value instanceof CharSequence) {
      this.dataType = DataType.STRING;
      this.value = ((CharSequence) value).toString();
    } else if (value instanceof Character) {
      this.dataType = DataType.STRING;
      this.value = ((Character) value).toString();
    } else if (value instanceof Boolean) {
      this.dataType = DataType.BOOLEAN;
      this.value = value;
    } else if (value instanceof AbstractSyntaxTreeNode) {
      this.dataType = DataType.EXPRESSION_NODE;
      this.value = value;
    } else {
      throw new IllegalArgumentException(
          "Неподдержимаемый тип данных '" + value.getClass().getName() + "'");
    }
  }

  public EvaluationValue(double value) {
    this.dataType = DataType.NUMBER;
    this.value = Double.toString(value);
  }

  public boolean isNumberValue() {
    return getDataType() == DataType.NUMBER;
  }

  public boolean isStringValue() {
    return getDataType() == DataType.STRING;
  }

  public boolean isBooleanValue() {
    return getDataType() == DataType.BOOLEAN;
  }

  public boolean isExpressionNode() {
    return getDataType() == DataType.EXPRESSION_NODE;
  }

  public static EvaluationValue numberOfString(String value) {
      return new EvaluationValue(Double.parseDouble(value));
  }

  public Double getNumberValue() {
    if (Objects.requireNonNull(getDataType()) == DataType.NUMBER) {
      return Double.valueOf((String) value);
    }
    return 0.0;
  }

  public String getStringValue() {
    return value.toString();
  }

  public Boolean getBooleanValue() {
    switch (getDataType()) {
      case BOOLEAN:
        return (Boolean) value;
      case STRING:
        return Boolean.parseBoolean((String) value);
      default:
        return false;
    }
  }
  public AbstractSyntaxTreeNode getExpressionNode() {
    return isExpressionNode() ? ((AbstractSyntaxTreeNode) getValue()) : null;
  }
  @Override
  public int compareTo(EvaluationValue toCompare) {
    switch (getDataType()) {
      case NUMBER:
        return getNumberValue().compareTo(toCompare.getNumberValue());
      case BOOLEAN:
        return getBooleanValue().compareTo(toCompare.getBooleanValue());
      default:
        return getStringValue().compareTo(toCompare.getStringValue());
    }
  }
}
