package ahodanenok.el.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

class BracketsAccessValueExpression extends ValueExpressionBase {

    final ValueExpression baseExpr;
    final ValueExpression propertyExpr;

    BracketsAccessValueExpression(
            ValueExpression baseExpr,
            ValueExpression propertyExpr) {
        this.baseExpr = baseExpr;
        this.propertyExpr = propertyExpr;
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
        Object base = baseExpr.getValue(context);
        if (base == null) {
            return null;
        }

        Object property = propertyExpr.getValue(context);
        if (property == null) {
            return null;
        }

        Object value = context.getELResolver().getValue(context, base, property);
        if (context.isPropertyResolved()) {
            return value;
        }

        return null;
    }

    @Override
    public void setValue(ELContext context, Object value) {
        Object base = baseExpr.getValue(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        Object property = propertyExpr.getValue(context);
        if (property == null) {
            throw propertyNotFoundException();
        }

        context.getELResolver().setValue(context, base, property, value);
        if (!context.isPropertyResolved()) {
            throw propertyNotFoundException();
        }
    }

    @Override
    public Class<?> getType(ELContext context) {
        Object base = baseExpr.getValue(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        Object property = propertyExpr.getValue(context);
        if (property == null) {
            throw propertyNotFoundException();
        }

        Class<?> type = context.getELResolver().getType(context, base, property);
        if (context.isPropertyResolved()) {
            return type;
        }

        throw propertyNotFoundException();
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        Object base = baseExpr.getValue(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        Object property = propertyExpr.getValue(context);
        if (property == null) {
            throw propertyNotFoundException();
        }

        boolean readOnly = context.getELResolver().isReadOnly(context, base, property);
        if (context.isPropertyResolved()) {
            return readOnly;
        }

        throw propertyNotFoundException();
    }

    @Override
    public ValueReference getValueReference(ELContext context) {
        Object base = baseExpr.getValue(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        Object property = propertyExpr.getValue(context);
        if (property == null) {
            throw propertyNotFoundException();
        }

        return new ValueReference(base, property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseExpr, propertyExpr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != BracketsAccessValueExpression.class) {
            return false;
        }

        BracketsAccessValueExpression other = (BracketsAccessValueExpression) obj;
        return baseExpr.equals(other.baseExpr) && propertyExpr.equals(other.propertyExpr);
    }

    private PropertyNotFoundException propertyNotFoundException() {
        return new PropertyNotFoundException("Trying to dereference null value");
    }
}
