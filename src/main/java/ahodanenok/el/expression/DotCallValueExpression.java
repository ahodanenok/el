package ahodanenok.el.expression;

import java.lang.reflect.Method;
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

class DotCallValueExpression extends ValueExpressionBase {

    final ValueExpression baseExpr;
    final String methodName;
    final List<ValueExpressionBase> args;

    DotCallValueExpression(
            ValueExpression baseExpr,
            String methodName,
            List<ValueExpressionBase> args) {
        this.baseExpr = baseExpr;
        this.methodName = methodName;
        this.args = args;
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

        Object[] args = null;
        if (base != IdentifierValueExpression.NOT_RESOLVED) {
            args = evaluateArgs(context);
            Object value = context.getELResolver().invoke(
                context, base, methodName, null, args);
            if (context.isPropertyResolved()) {
                return value;
            }
        }

        Method method = resolveStaticMethod(context);
        if (method != null) {
            if (args == null) {
                args = evaluateArgs(context);
            }

            try {
                return method.invoke(null, args);
            } catch (Exception e) {
                throw new ELException(
                    "Failed to invoke method '%s'".formatted(method), e);
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
            context.getELResolver().setValue(context, base, methodName, value);
            if (context.isPropertyResolved()) {
                return;
            }
        }

        Method method = resolveStaticMethod(context);
        if (method != null) {
            throw new PropertyNotWritableException("");
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
            Class<?> type = context.getELResolver().getType(context, base, methodName);
            if (context.isPropertyResolved()) {
                return type;
            }
        }

        if (resolveStaticMethod(context) != null) {
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
            boolean readOnly = context.getELResolver().isReadOnly(context, base, methodName);
            if (context.isPropertyResolved()) {
                return readOnly;
            }
        }

        if (resolveStaticMethod(context) != null) {
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
            return new ValueReference(idExpr.name, methodName);
        } else {
            return new ValueReference(base, methodName);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseExpr, methodName, args);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != DotCallValueExpression.class) {
            return false;
        }

        DotCallValueExpression other = (DotCallValueExpression) obj;
        return baseExpr.equals(other.baseExpr)
            && methodName.equals(other.methodName)
            && args.equals(other.args);
    }

    private PropertyNotFoundException propertyNotFoundException() {
        return new PropertyNotFoundException("Trying to dereference null value");
    }

    private Class<?>[] collectArgTypes(Object[] args, String methodName) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new ELException(
                    "Can't resolve static method '%s' because an argument at the position %d is null"
                        .formatted(methodName, i));
            }

            argTypes[i] = args[i].getClass();
        }

        return argTypes;
    }

    private Object[] evaluateArgs(ELContext context) {
        Object[] values = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            values[i] = args.get(i).getValue(context);
        }

        return values;
    }

    private Object getBase(ELContext context) {
        if (baseExpr instanceof IdentifierValueExpression idExpr) {
            return idExpr.tryGetValue(context);
        } else {
            return baseExpr.getValue(context);
        }
    }

    private Method resolveStaticMethod(ELContext context) {
        String className;
        if (baseExpr instanceof IdentifierValueExpression idExpr) {
            className = idExpr.name;
        } else {
            return null;
        }

        Class<?> resolvedClass = context.getImportHandler().resolveClass(className);
        if (resolvedClass == null) {
            return null;
        }

        List<Method> candidateMethods = new ArrayList<>();
        Method[] methods = resolvedClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(methodName)
                    && Modifier.isPublic(method.getModifiers())
                    && Modifier.isStatic(method.getModifiers())) {
                candidateMethods.add(method);
            }
        }

        Object[] args = evaluateArgs(context);
        Method method;
        if (candidateMethods.size() == 1) {
            method = candidateMethods.get(0);
        } else {
            try {
                method = resolvedClass.getMethod(className, collectArgTypes(args, methodName));
            } catch (Exception e) {
                return null;
            }
        }

        if (!Modifier.isPublic(method.getModifiers())
                || !Modifier.isStatic(method.getModifiers())) {
            return null;
        }

        return method;
    }
}
