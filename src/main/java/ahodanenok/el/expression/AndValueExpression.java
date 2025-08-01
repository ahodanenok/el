package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class AndValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    AndValueExpression(ValueExpression left, ValueExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ELContext context) {
        try {
            return (T) getValueInternal(context);
        } catch (ELException e) {
            throw e;
        } catch (Exception e) {
            throw new ELException("Failed to evalulate expression", e);
        }
    }

    private Boolean getValueInternal(ELContext context) {
        Object leftValue = left.getValue(context);
        Object rightValue = right.getValue(context);
        if (!context.convertToType(leftValue, Boolean.class)) {
            return Boolean.FALSE;
        }

        return context.convertToType(rightValue, Boolean.class);
    }


    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != AndValueExpression.class) {
            return false;
        }

        AndValueExpression other = (AndValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
