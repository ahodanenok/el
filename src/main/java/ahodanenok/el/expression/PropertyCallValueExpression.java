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

class PropertyCallValueExpression extends ValueExpressionBase {

    final ValueExpression left;
    final ValueExpression right;
    final List<ValueExpression> params;
    final boolean allowStatic;

    PropertyCallValueExpression(ValueExpression left, ValueExpression right, List<ValueExpression> params, boolean allowStatic) {
        this.left = left;
        this.right = right;
        this.params = params;
        this.allowStatic = allowStatic;
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
            Object[] paramValues = new Object[params.size()];
            for (int i = 0; i < params.size(); i++) {
                paramValues[i] = params.get(i).getValue(context);
            }

            Object value = context.getELResolver().invoke(
                context, ref.getBase(), ref.getProperty(), null, paramValues);
            if (context.isPropertyResolved()) {
                return value;
            }
        }

        if (allowStatic
                && left instanceof IdentifierValueExpression classId
                && right instanceof StaticValueExpression methodNameExpr
                && methodNameExpr.value instanceof String methodName) {
            String className = classId.name;
            Class<?> resolvedClass = context.getImportHandler().resolveClass(className);
            if (resolvedClass != null) {
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
                        throw new ELException(
                            "Function with name '%s' wasn't resolved".formatted(methodName), e);
                    }
                }

                if (Modifier.isPublic(method.getModifiers())
                        && Modifier.isStatic(method.getModifiers())) {
                    try {
                        return method.invoke(null, args);
                    } catch (Exception e) {
                        throw new ELException(
                            "Failed to invoke method '%s'".formatted(method), e);
                    }
                }
            }
        }

        return null;
    }

    private Object[] evaluateArgs(ELContext context) {
        Object[] values = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            values[i] = params.get(i).getValue(context);
        }

        return values;
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
