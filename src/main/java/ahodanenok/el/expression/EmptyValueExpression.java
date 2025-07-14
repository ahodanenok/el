package ahodanenok.el.expression;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

class EmptyValueExpression extends ValueExpressionBase {

    final ValueExpression expr;

    EmptyValueExpression(ValueExpression expr) {
        this.expr = Objects.requireNonNull(expr);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ELContext context) {
        Object value = expr.getValue(context);
        if (value == null) {
            return (T) Boolean.TRUE;
        } else if (value instanceof String obj) {
            return (T) Boolean.valueOf(obj.isEmpty());
        } else if (value instanceof Map obj) {
            return (T) Boolean.valueOf(obj.isEmpty());
        } else if (value instanceof Collection obj) {
            return (T) Boolean.valueOf(obj.isEmpty());
        } else if (value.getClass().isArray()) {
            return (T) Boolean.valueOf(Array.getLength(value) == 0);
        } else {
            return (T) Boolean.FALSE;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != EmptyValueExpression.class) {
            return false;
        }

        EmptyValueExpression other = (EmptyValueExpression) obj;
        return expr.equals(other.expr);
    }

    @Override
    public int hashCode() {
        return expr.hashCode();
    }
}
