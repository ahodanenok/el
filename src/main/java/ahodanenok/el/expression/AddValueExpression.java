package ahodanenok.el.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

import ahodanenok.el.Ops;

class AddValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    AddValueExpression(ValueExpression left, ValueExpression right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    @Override
    public <T> T getValue(ELContext context) {
        try {
            return convertIfNecessary(context, getValueInternal(context));
        } catch (ELException e) {
            throw e;
        } catch (Exception e) {
            throw new ELException("Failed to add", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValueInternal(ELContext context) {
        return (T) Ops.add(context, left.getValue(context), right.getValue(context));
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != AddValueExpression.class) {
            return false;
        }

        AddValueExpression other = (AddValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
