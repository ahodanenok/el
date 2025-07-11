package ahodanenok.el.expression;

import java.io.StringReader;

import ahodanenok.el.token.Tokenizer;
import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;

public class ExpressionFactoryImpl extends ExpressionFactory {

    @Override
    public <T> T coerceToType(Object obj, Class<T> targetType) {
        if (obj == null) {
            return null;
        }

        if (targetType.isAssignableFrom(obj.getClass())) {
            return targetType.cast(obj);
        }

        throw new ELException(
            "Can't coerce object of type '%s' to '%s'".formatted(obj.getClass(), targetType));
    }

    @Override
    public MethodExpression createMethodExpression(ELContext context, String arg1, Class<?> arg2, Class<?>[] arg3) {
        return null;
    }

    @Override
    public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
        StaticValueExpressionImpl expr = new StaticValueExpressionImpl(instance);
        expr.expectedType = expectedType;
        return expr;
    }

    @Override
    public ValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(expression));
        Parser parser = new Parser(tokenizer);

        ValueExpressionBase expr = parser.composite();
        expr.expressionString = expression;
        expr.expectedType = expectedType;

        return expr;
    }
}
