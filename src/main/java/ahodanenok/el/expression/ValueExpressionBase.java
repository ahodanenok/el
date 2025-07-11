package ahodanenok.el.expression;

import jakarta.el.ELContext;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueExpression;

public abstract class ValueExpressionBase extends ValueExpression {

    String expressionString;
    Class<?> expectedType;

    @Override
    public Class<?> getType(ELContext context) {
        return null;
    }

    @Override
    public final Class<?> getExpectedType() {
        return expectedType;
    }

    @Override
    public void setValue(ELContext context, Object value) {
        throw new PropertyNotWritableException();
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        return true;
    }

    @Override
    public final String getExpressionString() {
        return expressionString;
    }

    @Override
    public final boolean isLiteralText() {
        // todo: what is it?
        return false;
    }

    @SuppressWarnings("unchecked")
    protected final <T> T convertIfNecessary(ELContext context, Object value) {
        if (expectedType != null) {
            return (T) context.convertToType(value, expectedType);
        } else {
            return (T) value;
        }
    }
}
