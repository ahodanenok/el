package ahodanenok.el.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class EqualValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    EqualValueExpression(ValueExpression left, ValueExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T getValue(ELContext context) {
        try {
            return getValueInternal(context);
        } catch (ELException e) {
            throw e;
        } catch (Exception e) {
            throw new ELException("Failed to compare values", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValueInternal(ELContext context) {
        Object leftValue = left.getValue(context);
        Object rightValue = right.getValue(context);
        if (leftValue == rightValue) {
            return (T) Boolean.TRUE;
        } else if (leftValue == null || rightValue == null) {
            return (T) Boolean.FALSE;
        } else if (leftValue instanceof BigDecimal
                || rightValue instanceof BigDecimal) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, BigDecimal.class)
                    .equals(context.convertToType(rightValue, BigDecimal.class)));
        } else if (leftValue instanceof Float
                || leftValue instanceof Double
                || rightValue instanceof Float
                || rightValue instanceof Double) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, Double.class)
                    .equals(context.convertToType(rightValue, Double.class)));
        } else if (leftValue instanceof BigInteger
                || rightValue instanceof BigInteger) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, BigInteger.class)
                    .equals(context.convertToType(rightValue, BigInteger.class)));
        } else if (leftValue instanceof Long
                || leftValue instanceof Integer
                || leftValue instanceof Short
                || leftValue instanceof Byte
                || leftValue instanceof Character
                || rightValue instanceof Long
                || rightValue instanceof Integer
                || rightValue instanceof Short
                || rightValue instanceof Byte
                || rightValue instanceof Character) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, Long.class)
                    .equals(context.convertToType(rightValue, Long.class)));
        } else if (leftValue instanceof Boolean
                || rightValue instanceof Boolean) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, Boolean.class)
                    .equals(context.convertToType(rightValue, Boolean.class)));
        } else if (leftValue instanceof Enum) {
            return (T) Boolean.valueOf(
                leftValue.equals(context.convertToType(rightValue, leftValue.getClass())));
        } else if (rightValue instanceof Enum) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, rightValue.getClass()).equals(rightValue));
        } else if (leftValue instanceof String
                || rightValue instanceof String) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, String.class)
                    .equals(context.convertToType(rightValue, String.class)));
        } else {
            return (T) Boolean.valueOf(leftValue.equals(rightValue));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != EqualValueExpression.class) {
            return false;
        }

        EqualValueExpression other = (EqualValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
