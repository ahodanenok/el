package ahodanenok.el.expression;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

class DotAccessValueExpression extends ValueExpressionBase {

    final ValueExpression baseExpr;
    final String property;

    DotAccessValueExpression(ValueExpression baseExpr, String property) {
        this.baseExpr = baseExpr;
        this.property = property;
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
        Object base = getBase(context);
        if (base == null) {
            return null;
        }

        if (base != IdentifierValueExpression.NOT_RESOLVED) {
            Object value = context.getELResolver().getValue(context, base, property);
            if (context.isPropertyResolved()) {
                return value;
            }
        }

        Field field = resolveStaticField(context);
        if (field != null) {
            try {
                return field.get(null);
            } catch (Exception e) {
                throw new ELException("Failed to get value from static field '%s'".formatted(field), e);
            }
        }

        return null;
    }

    @Override
    public void setValue(ELContext context, Object value) {
        Object base = getBase(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        if (base != IdentifierValueExpression.NOT_RESOLVED) {
            context.getELResolver().setValue(context, base, property, value);
            if (context.isPropertyResolved()) {
                return;
            }
        }

        Field field = resolveStaticField(context);
        if (field != null) {
            throw new PropertyNotWritableException("Static fields are not writeable");
        }

        throw propertyNotFoundException();
    }

    @Override
    public Class<?> getType(ELContext context) {
        Object base = getBase(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        if (base != IdentifierValueExpression.NOT_RESOLVED) {
            Class<?> type = context.getELResolver().getType(context, base, property);
            if (context.isPropertyResolved()) {
                return type;
            }
        }

        if (resolveStaticField(context) != null) {
            return null;
        }

        throw propertyNotFoundException();
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        Object base = getBase(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        if (base != IdentifierValueExpression.NOT_RESOLVED) {
            boolean readOnly = context.getELResolver().isReadOnly(context, base, property);
            if (context.isPropertyResolved()) {
                return readOnly;
            }
        }

        if (resolveStaticField(context) != null) {
            return true;
        }

        throw propertyNotFoundException();
    }

    @Override
    public ValueReference getValueReference(ELContext context) {
        Object base = getBase(context);
        if (base == null) {
            throw propertyNotFoundException();
        }

        if (baseExpr instanceof IdentifierValueExpression idExpr
                && base == IdentifierValueExpression.NOT_RESOLVED) {
            return new ValueReference(idExpr.name, property);
        } else {
            return new ValueReference(base, property);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseExpr, property);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != DotAccessValueExpression.class) {
            return false;
        }

        DotAccessValueExpression other = (DotAccessValueExpression) obj;
        return baseExpr.equals(other.baseExpr) && property.equals(other.property);
    }

    private PropertyNotFoundException propertyNotFoundException() {
        return new PropertyNotFoundException("Trying to dereference null value");
    }

    private Object getBase(ELContext context) {
        if (baseExpr instanceof IdentifierValueExpression idExpr) {
            return idExpr.tryGetValue(context);
        } else {
            return baseExpr.getValue(context);
        }
    }

    private Field resolveStaticField(ELContext context) {
        String className;
        if (baseExpr instanceof IdentifierValueExpression idExpr) {
            className = idExpr.name;
        } else {
            return null;
        }

        Class<?> resolvedClass = context.getImportHandler().resolveClass(className);
        Field field = null;
        if (resolvedClass != null) {
            try {
                field = resolvedClass.getField(property);
            } catch (NoSuchFieldException e) {
                field = null;
            }
        }

        if (field == null
                || !Modifier.isPublic(field.getModifiers())
                || !Modifier.isStatic(field.getModifiers())) {
            return null;
        }

        return field;
    }
}
