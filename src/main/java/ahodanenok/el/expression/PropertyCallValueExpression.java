package ahodanenok.el.expression;

import java.util.List;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

class PropertyCallValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;
    final List<ValueExpression> params;

    PropertyCallValueExpression(ValueExpression left, ValueExpression right, List<ValueExpression> params) {
        this.left = left;
        this.right = right;
        this.params = params;
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
        ValueReference ref = resolveProperty(context);
        if (ref == null) {
            return null;
        }

        Object[] paramValues = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            paramValues[i] = params.get(i).getValue(context);
        }

        return context.getELResolver().invoke(
            context, ref.getBase(), ref.getProperty(), null, paramValues);
    }

    // @Override
    // public ValueReference getValueReference(ELContext context) {
    //     ValueReference ref = resolveProperty(context);
    //     if (ref == null) {
    //         throw new PropertyNotFoundException();
    //     }

    //     return ref;
    // }

    private ValueReference resolveProperty(ELContext context) {
        Object base = left.getValue(context);
        if (base == null) {
            return null;
        }

        Object property = right.getValue(context);
        if (property == null) {
            return null;
        }

        return new ValueReference(base, property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != PropertyCallValueExpression.class) {
            return false;
        }

        PropertyCallValueExpression other = (PropertyCallValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
