package ahodanenok.el.expression;

import jakarta.el.ELContext;
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
}
