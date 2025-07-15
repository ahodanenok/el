package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;

class ExpressionFactoryStubs {

    static final ExpressionFactory NONE = new EmptyExpressionFactory();

    static ExpressionFactory coerceToValue(Object convertedValue, Class<?> expectedTargetClass) {
        return new EmptyExpressionFactory() {
            @Override
            public <T> T coerceToType(Object value, Class<T> targetClass) {
                if (targetClass.equals(expectedTargetClass)) {
                    return (T) convertedValue;
                } else {
                    throw new IllegalArgumentException("Unexpected target class: " + targetClass);
                }
            }
        };
    }

    static ExpressionFactory coerceToValue(ConversionRule... rules) {
        return new EmptyExpressionFactory() {
            @Override
            public <T> T coerceToType(Object value, Class<T> targetClass) {
                for (ConversionRule rule : rules) {
                    if (Objects.equals(value, rule.expectedValue)
                            && Objects.equals(targetClass, rule.targetClass)) {
                        return (T) rule.targetValue;
                    }
                }

                throw new IllegalArgumentException(
                    "Unexpected conversion of value '%s' to '%s'".formatted(value, targetClass));
            }
        };
    }

    static ExpressionFactory elException(String msg) {
        return new EmptyExpressionFactory() {
            @Override
            public <T> T coerceToType(Object value, Class<T> targetClass) {
                throw new ELException(msg);
            }
        };
    }

    static class EmptyExpressionFactory extends ExpressionFactory {

        @Override
        public <T> T coerceToType(Object value, Class<T> targetClass) {
            throw new IllegalStateException("Not expected");
        }

        @Override
        public MethodExpression createMethodExpression(ELContext context, String expr, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
            throw new IllegalStateException("Not expected");
        }

        @Override
        public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
            throw new IllegalStateException("Not expected");
        }

        @Override
        public ValueExpression createValueExpression(ELContext context, String expr, Class<?> expectedType) {
            throw new IllegalStateException("Not expected");
        }
    }

    static final class ConversionRule {

        private final Object expectedValue;
        private final Object targetValue;
        private final Class<?> targetClass;

        ConversionRule(Object expectedValue, Object targetValue, Class<?> targetClass) {
            this.expectedValue = expectedValue;
            this.targetValue = targetValue;
            this.targetClass = targetClass;
        }
    }
}
