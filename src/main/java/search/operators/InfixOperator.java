package search.operators;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InfixOperator {

  int precedence();

  boolean leftAssociative() default true;
}
