package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

class AssignValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    AssignValueExpression(ValueExpression left, ValueExpression right) {
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

    private Object getValueInternal(ELContext context) {
        ValueReference ref;
        if (left instanceof IdentifierValueExpression id) {
            if (context.isLambdaArgument(id.name)) {
                throw new PropertyNotWritableException("Identifier '%s' is a lambda argument".formatted(id.name));
            } else if (id.varExpr != null) {
                ref = id.varExpr.getValueReference(context);
            } else {
                ref = new ValueReference(null, id.name);
            }
        } else {
            ref = left.getValueReference(context);
        }

        Object value = right.getValue(context);
        context.getELResolver().setValue(context, ref.getBase(), ref.getProperty(), value);

        return convertIfNecessary(context, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != AssignValueExpression.class) {
            return false;
        }

        AssignValueExpression other = (AssignValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
