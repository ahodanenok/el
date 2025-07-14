package ahodanenok.el.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

public class NegateValueExpression extends ValueExpressionBase {

    private final ValueExpression expr;

    public NegateValueExpression(ValueExpression expr) {
        this.expr = expr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ELContext context) {
        Object value = expr.getValue(context);
        if (value == null) {
            return (T) Long.valueOf(0);
        } else if (value instanceof BigDecimal n) {
            return (T) n.negate();
        } else if (value instanceof BigInteger n) {
            return (T) n.negate();
        } else if (value instanceof String s) {
            if (ExpressionUtils.looksLikeDouble(s)) {
                return (T) Double.valueOf(-context.convertToType(s, Double.class).doubleValue());
            } else{
                return (T) Long.valueOf(-context.convertToType(s, Long.class).longValue());
            }
        } else if (value instanceof Byte n) {
            return (T) Byte.valueOf((byte) -n.byteValue());
        } else if (value instanceof Short n) {
            return (T) Short.valueOf((short) -n.shortValue());
        } else if (value instanceof Integer n) {
            return (T) Integer.valueOf(-n.intValue());
        } else if (value instanceof Long n) {
            return (T) Long.valueOf((long) -n.longValue());
        } else if (value instanceof Float n) {
            return (T) Float.valueOf(-n.floatValue());
        } else if (value instanceof Double n) {
            return (T) Double.valueOf(-n.doubleValue());
        } else {
            throw new ELException("Numeric negation is not defined for type '%s'"
                .formatted(value.getClass().getName()));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        NegateValueExpression other = (NegateValueExpression) obj;
        return other.expr.equals(expr);
    }

    @Override
    public int hashCode() {
        return expr.hashCode();
    }
}
