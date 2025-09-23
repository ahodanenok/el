package ahodanenok.el.expression;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

class PropertyAccessValueExpression extends ValueExpressionBase {

    final ValueExpression baseExpr;
    final String propertyName;
    final ValueExpression propertyExpr;

    PropertyAccessValueExpression(ValueExpression baseExpr, String propertyName, ValueExpression propertyExpr) {
        this.baseExpr = baseExpr;
        this.propertyName = propertyName;
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
        ValueReference ref = resolveProperty(context);
        if (ref != null) {
            Object value = context.getELResolver().getValue(context, ref.getBase(), ref.getProperty());
            if (context.isPropertyResolved()) {
                return value;
            }
        }

System.out.println("!!! %s - %s".formatted(baseExpr, propertyName));
        if (baseExpr instanceof IdentifierValueExpression baseId && propertyName != null) {

            Field field = resolveStaticField(context, baseId.name);
System.out.println("!!! field=" + field);
            try {
                return field.get(null);
            } catch (Exception e) {
                throw new ELException("Failed to get value from static field '%s'".formatted(field), e);
            }
        }

        return null;
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        ValueReference ref = resolveProperty(context);
        if (ref != null) {
            boolean readOnly = context.getELResolver().isReadOnly(context, ref.getBase(), ref.getProperty());
            if (context.isPropertyResolved()) {
                return readOnly;
            }
        }

        return true;
    }

    @Override
    public Class<?> getType(ELContext context) {
        ValueReference ref = resolveProperty(context);
        if (ref != null) {
            Class<?> type = context.getELResolver().getType(context, ref.getBase(), ref.getProperty());
            if (context.isPropertyResolved()) {
                return type;
            }
        }

        return null;
    }

    @Override
    public void setValue(ELContext context, Object value) {
        ValueReference ref = resolveProperty(context);
        if (ref != null) {
            context.getELResolver().setValue(context, ref.getBase(), ref.getProperty(), value);
            if (context.isPropertyResolved()) {
                return;
            }
        }

        if (baseExpr instanceof IdentifierValueExpression baseId && propertyName != null) {
            resolveStaticField(context, baseId.name);
            throw new PropertyNotWritableException("Static fields are not writeable");
        }

        // todo: better error message
        throw new PropertyNotFoundException("Property not found");
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
        Object base = baseExpr.getValue(context);
        if (base == null) {
            return null;
        }

        Object property = propertyExpr.getValue(context);
        if (property == null) {
            return null;
        }

        return new ValueReference(base, property);
    }

    private Field resolveStaticField(ELContext context, String className) {
        Class<?> resolvedClass = context.getImportHandler().resolveClass(className);

        Field field = null;
        if (resolvedClass != null) {
            try {
                field = resolvedClass.getField(propertyName);
            } catch (NoSuchFieldException e) {
                // it's okay
                field = null;
            }
        }

        if (field == null
                || !Modifier.isPublic(field.getModifiers())
                || !Modifier.isStatic(field.getModifiers())) {
            throw new ELException("Property '%s.%s' wasn't resolved".formatted(className, propertyName));
        }

        return field;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseExpr, propertyName, propertyExpr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != PropertyAccessValueExpression.class) {
            return false;
        }

        PropertyAccessValueExpression other = (PropertyAccessValueExpression) obj;
        return baseExpr.equals(other.baseExpr)
            && Objects.equals(propertyName, other.propertyName)
            && propertyExpr.equals(other.propertyExpr);
    }
}
