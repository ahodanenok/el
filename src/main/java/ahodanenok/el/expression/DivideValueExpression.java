package ahodanenok.el.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class DivideValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    DivideValueExpression(ValueExpression left, ValueExpression right) {
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
            throw new ELException("Failed to divide", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValueInternal(ELContext context) {
        Object leftValue = left.getValue(context);
        Object rightValue = right.getValue(context);
        if (leftValue == null && rightValue == null) {
            return (T) Long.valueOf(0L);
        } else if (leftValue instanceof BigDecimal
                || leftValue instanceof BigInteger
                || rightValue instanceof BigDecimal
                || rightValue instanceof BigInteger) {
            return (T) context.convertToType(leftValue, BigDecimal.class)
                .divide(context.convertToType(rightValue, BigDecimal.class), RoundingMode.HALF_UP);
        } else {
            return (T) Double.valueOf(
                context.convertToType(leftValue, Double.class)
                / context.convertToType(rightValue, Double.class));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != DivideValueExpression.class) {
            return false;
        }

        DivideValueExpression other = (DivideValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
