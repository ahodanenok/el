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

public class IdentifierValueExpression extends ValueExpressionBase {

    static final Object NOT_RESOLVED = new Object();

    final String name;
    final ValueExpression varExpr;

    IdentifierValueExpression(String name, ValueExpression varExpr) {
        this.name = name;
        this.varExpr = varExpr;
    }

    @Override
    public Class<?> getType(ELContext context) {
        if (context.isLambdaArgument(name)) {
            return null;
        } else if (varExpr != null) {
            return varExpr.getType(context);
        } else {
            Class<?> type = context.getELResolver().getType(context, null, name);
            if (context.isPropertyResolved()) {
                return type;
            }

            if (resolveStaticField(context) != null) {
                return null;
            }

            throw new PropertyNotFoundException(name);
        }
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        if (context.isLambdaArgument(name)) {
            return true;
        } else if (varExpr != null) {
            return varExpr.isReadOnly(context);
        } else {
            boolean readOnly = context.getELResolver().isReadOnly(context, null, name);
            if (context.isPropertyResolved()) {
                return readOnly;
            }

            if (resolveStaticField(context) != null) {
                return true;
            }

            throw new PropertyNotFoundException(name);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ELContext context) {
        Object value = tryGetValue(context);
        if (value == NOT_RESOLVED) {
            throw new PropertyNotFoundException(name);
        }

        return (T) convertIfNecessary(context, value);
    }

    Object tryGetValue(ELContext context) {
        Object value;
        if (context.isLambdaArgument(name)) {
            value = context.getLambdaArgument(name);
        } else if (varExpr != null) {
            value = varExpr.getValue(context);
        } else {
            value = context.getELResolver().getValue(context, null, name);
            if (!context.isPropertyResolved()) {
                Field field = resolveStaticField(context);
                if (field != null) {
                    try {
                        value = field.get(null);
                    } catch (Exception e) {
                        throw new ELException("Failed to get value from field '%s.%s'".formatted(
                            field.getDeclaringClass().getName(), field.getName()), e);
                    }
                } else {
                    value = NOT_RESOLVED;
                }
            }
        }

        return value;
    }

    @Override
    public void setValue(ELContext context, Object value) {
        if (context.isLambdaArgument(name)) {
            throw new PropertyNotWritableException("'%s' is a lambda argument".formatted(name));
        } else if (varExpr != null) {
            varExpr.setValue(context, value);
        } else {
            context.getELResolver().setValue(context, null, name, value);
            if (!context.isPropertyResolved()) {
                if (resolveStaticField(context) != null) {
                    throw new PropertyNotWritableException(name);
                }

                throw new PropertyNotFoundException(name);
            }
        }
    }

    @Override
    public ValueReference getValueReference(ELContext context) {
        if (varExpr != null) {
            return varExpr.getValueReference(context);
        } else {
            return new ValueReference(null, name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.hashCode(), varExpr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != IdentifierValueExpression.class) {
            return false;
        }

        IdentifierValueExpression other = (IdentifierValueExpression) obj;
        return name.equals(other.name) && Objects.equals(varExpr, other.varExpr);
    }

    private Field resolveStaticField(ELContext context) {
        Class<?> resolvedClass = context.getImportHandler().resolveStatic(name);
        if (resolvedClass == null) {
            return null;
        }

        Field field;
        try {
            field = resolvedClass.getField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }

        if (!Modifier.isPublic(field.getModifiers())
                || !Modifier.isStatic(field.getModifiers())) {
            return null;
        }

        return field;
    }
}
