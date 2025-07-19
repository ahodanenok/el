package ahodanenok.el.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class ModuloValueExpression extends ValueExpressionBase {

    private final ValueExpression left;
    private final ValueExpression right;

    ModuloValueExpression(ValueExpression left, ValueExpression right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    @Override
    public <T> T getValue(ELContext context) {
        try {
            return getValueInternal(context);
        } catch (ELException e) {
            throw e;
        } catch (Exception e) {
            throw new ELException("Modulo operator failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValueInternal(ELContext context) {
        Object leftValue = left.getValue(context);
        Object rightValue = right.getValue(context);
        if (leftValue == null && rightValue == null) {
            return (T) Long.valueOf(0L);
        } else if (leftValue instanceof BigDecimal
                || leftValue instanceof Double
                || leftValue instanceof Float
                || (leftValue instanceof String s && ExpressionUtils.looksLikeDouble(s))
                || rightValue instanceof BigDecimal
                || rightValue instanceof Double
                || rightValue instanceof Float
                || (rightValue instanceof String s && ExpressionUtils.looksLikeDouble(s))) {
            return (T) Double.valueOf(
                context.convertToType(leftValue, Double.class)
                % context.convertToType(rightValue, Double.class));
        } else if (leftValue instanceof BigInteger || rightValue instanceof BigInteger) {
            return (T) context.convertToType(leftValue, BigInteger.class)
                .remainder(context.convertToType(rightValue, BigInteger.class));
        } else {
            return (T) Long.valueOf(
                context.convertToType(leftValue, Long.class)
                % context.convertToType(rightValue, Long.class));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != ModuloValueExpression.class) {
            return false;
        }

        ModuloValueExpression other = (ModuloValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
