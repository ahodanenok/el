package ahodanenok.el.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class LessThanValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    LessThanValueExpression(ValueExpression left, ValueExpression right) {
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
            e.printStackTrace();
            throw new ELException("Failed to compare values", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValueInternal(ELContext context) {
        Object leftValue = left.getValue(context);
        Object rightValue = right.getValue(context);
        if (leftValue == null || rightValue == null) {
            return (T) Boolean.FALSE;
        } else if (leftValue instanceof BigDecimal
                || rightValue instanceof BigDecimal) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, BigDecimal.class)
                    .compareTo(context.convertToType(rightValue, BigDecimal.class)) < 0);
        } else if (leftValue instanceof Double
                || leftValue instanceof Float
                || rightValue instanceof Double
                || rightValue instanceof Float) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, Double.class)
                    < context.convertToType(rightValue, Double.class));
        } else if (leftValue instanceof BigInteger
                || rightValue instanceof BigInteger) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, BigInteger.class)
                    .compareTo(context.convertToType(rightValue, BigInteger.class)) < 0);
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
                    < context.convertToType(rightValue, Long.class));
        } else if (leftValue instanceof String || rightValue instanceof String) {
            return (T) Boolean.valueOf(
                context.convertToType(leftValue, String.class)
                    .compareTo(context.convertToType(rightValue, String.class)) < 0);
        } else if (leftValue instanceof Comparable cmp) {
            return (T) Boolean.valueOf(cmp.compareTo(rightValue) < 0);
        } else if (rightValue instanceof Comparable cmp) {
            return (T) Boolean.valueOf(cmp.compareTo(leftValue) > 0);
        } else {
            throw new ELException("Comparison of types '%s' and '%s' is not supported"
                .formatted(leftValue.getClass().getName(), rightValue.getClass().getName()));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != LessThanValueExpression.class) {
            return false;
        }

        LessThanValueExpression other = (LessThanValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
