package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

class PropertyAccessValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;

    PropertyAccessValueExpression(ValueExpression left, ValueExpression right) {
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
        ValueReference ref = resolveProperty(context);
        if (ref == null) {
            return null;
        }

        return context.getELResolver().getValue(context, ref.getBase(), ref.getProperty());
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        ValueReference ref = resolveProperty(context);
        if (ref == null) {
            return true;
        }

        return context.getELResolver().isReadOnly(context, ref.getBase(), ref.getProperty());
    }

    @Override
    public Class<?> getType(ELContext context) {
        ValueReference ref = resolveProperty(context);
        if (ref == null) {
            return null;
        }

        return context.getELResolver().getType(context, ref.getBase(), ref.getProperty());
    }

    @Override
    public void setValue(ELContext context, Object value) {
        ValueReference ref = resolveProperty(context);
        if (ref == null) {
            throw new PropertyNotWritableException();
        }

        context.getELResolver().setValue(context, ref.getBase(), ref.getProperty(), value);
    }

    @Override
    public ValueReference getValueReference(ELContext context) {
        ValueReference ref = resolveProperty(context);
        if (ref == null) {
            throw new PropertyNotFoundException();
        }

        return ref;
    }

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
        if (obj == null || obj.getClass() != PropertyAccessValueExpression.class) {
            return false;
        }

        PropertyAccessValueExpression other = (PropertyAccessValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
