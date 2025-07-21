package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class ConcatenateValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    ConcatenateValueExpression(ValueExpression left, ValueExpression right) {
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
            throw new ELException("Failed to concatenate values", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValueInternal(ELContext context) {
        Object leftValue = left.getValue(context);
        Object rightValue = right.getValue(context);
        return (T) (context.convertToType(leftValue, String.class)
            + context.convertToType(rightValue, String.class));
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != ConcatenateValueExpression.class) {
            return false;
        }

        ConcatenateValueExpression other = (ConcatenateValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
