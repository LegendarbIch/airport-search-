package search.config;

import lombok.Builder;
import lombok.Getter;
import search.data.DataAccessor;
import search.data.MapBasedDataAccessor;
import search.operators.booleans.*;

import java.util.Map;
import java.util.function.Supplier;

@Builder
public class ExpressionConfiguration {

  @Builder.Default
  @Getter
  @SuppressWarnings("unchecked")
  private final OperatorDictionary operatorDictionary =
      MapBasedOperatorDictionary.ofOperators(
          // booleans
          Map.entry("=", new InfixEqualsOperator()),
          Map.entry("<>", new InfixNotEqualsOperator()),
          Map.entry(">", new InfixGreaterOperator()),
          Map.entry(">=", new InfixGreaterEqualsOperator()),
          Map.entry("<", new InfixLessOperator()),
          Map.entry("<=", new InfixLessEqualsOperator()),
          Map.entry("&", new InfixAndOperator()),
          Map.entry("||", new InfixOrOperator()));

  @Builder.Default @Getter
  private final Supplier<DataAccessor> dataAccessorSupplier = MapBasedDataAccessor::new;

  public static ExpressionConfiguration defaultConfiguration() {
    return ExpressionConfiguration.builder().build();
  }
}
