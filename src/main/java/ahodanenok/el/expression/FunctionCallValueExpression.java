package ahodanenok.el.expression;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.LambdaExpression;
import jakarta.el.ValueExpression;

class FunctionCallValueExpression extends ValueExpressionBase {

    final String prefix;
    final String localName;
    final ValueExpression variableExpr;
    final ValueExpressionBase function;
    final Method mappedMethod;
    final List<ValueExpressionBase> args;

    FunctionCallValueExpression(String prefix, String localName, ValueExpression variableExpr, Method mappedMethod, List<ValueExpressionBase> args) {
        this.prefix = prefix;
        this.localName = localName;
        this.variableExpr = variableExpr;
        this.function = null;
        this.mappedMethod = mappedMethod;
        this.args = args;
    }

    FunctionCallValueExpression(ValueExpressionBase function, List<ValueExpressionBase> args) {
        this.prefix = null;
        this.localName = null;
        this.variableExpr = null;
        this.function = function;
        this.mappedMethod = null;
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
            e.printStackTrace();
            throw new ELException("Failed to evalulate expression", e);
        }
    }

    private Object getValueInternal(ELContext context) throws Exception {
        Object functionObj = null;
        if (function != null) {
            functionObj = function.getValue(context);
        } else if (prefix == null && mappedMethod == null) {
            if (context.isLambdaArgument(localName)) {
                functionObj = context.getLambdaArgument(localName);
            } else if (variableExpr != null) {
                functionObj = variableExpr.getValue(context);
            } else  {
                Object resolvedValue = context.getELResolver().getValue(context, null, localName);
                if (context.isPropertyResolved()) {
                    functionObj = resolvedValue;
                }
            }
        }

        if (functionObj instanceof LambdaExpression lambda) {
            return lambda.invoke(context, evaluateArgs(context, null));
        } else if (mappedMethod != null) {
            try {
                return mappedMethod.invoke(null, evaluateArgs(context, mappedMethod.getParameterTypes()));
            } catch (InvocationTargetException e) {
                if (e.getCause() != null) {
                    throw new ELException("Failed to invoke method", e.getCause());
                } else {
                    throw e;
                }
            }
        } else if (functionObj != null) {
            throw new ELException("Object of type '%s' can't be invoked as function"
                .formatted(functionObj.getClass().getName()));
        } else if (prefix == null) {
            Class<?> resolvedClass;

            resolvedClass = context.getImportHandler().resolveClass(localName);
            System.out.println("!!! " + resolvedClass);
            if (resolvedClass != null) {
                List<Constructor<?>> candidateConstructors = new ArrayList<>();
                Constructor<?>[] constructors = resolvedClass.getDeclaredConstructors();
                for (int i = 0; i < constructors.length; i++) {
                    Constructor<?> constructor = constructors[i];
                    if (constructor.getName().equals(localName)
                            && Modifier.isPublic(constructor.getModifiers())) {
                        candidateConstructors.add(constructor);
                    }
                }

                Object[] args = evaluateArgs(context, null);
                Constructor<?> constructor;
                if (candidateConstructors.size() == 1) {
                    constructor = candidateConstructors.get(0);
                } else {
                    try {
                        constructor = resolvedClass.getConstructor(collectArgTypes(args));
                    } catch (Exception e) {
                        throw new ELException(
                            "Function with name '%s' wasn't resolved".formatted(localName), e);
                    }
                }

                if (Modifier.isPublic(constructor.getModifiers())) {
                    try {
                        return constructor.newInstance(args);
                    } catch (Exception e) {
                        throw new ELException(
                            "Failed to invoke constructor '%s'".formatted(constructor), e);
                    }
                }
            }

            resolvedClass = context.getImportHandler().resolveStatic(localName);
            if (resolvedClass != null) {
                List<Method> candidateMethods = new ArrayList<>();
                Method[] methods = resolvedClass.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.getName().equals(localName)
                            && Modifier.isPublic(method.getModifiers())
                            && Modifier.isStatic(method.getModifiers())) {
                        candidateMethods.add(method);
                    }
                }

                Object[] args = evaluateArgs(context, null);
                Method method;
                if (candidateMethods.size() == 1) {
                    method = candidateMethods.get(0);
                } else {
                    try {
                        method = resolvedClass.getMethod(localName, collectArgTypes(args));
                    } catch (Exception e) {
                        throw new ELException(
                            "Function with name '%s' wasn't resolved".formatted(localName), e);
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

        throw new ELException("Function with name '%s' wasn't resolved"
            .formatted(prefix != null ? prefix + ":" + localName : localName));
    }

    private Object[] evaluateArgs(ELContext context, Class<?>[] expectedTypes) {
        Object[] values = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            Object value = args.get(i).getValue(context);
            if (expectedTypes != null) {
                value = context.convertToType(value, expectedTypes[i]);
            }
            values[i] = value;
        }

        return values;
    }

    private Class<?>[] collectArgTypes(Object[] args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new ELException(
                    "Can't resolve static method '%s' because an argument at the position %d is null"
                        .formatted(localName, i));
            }

            argTypes[i] = args[i].getClass();
        }

        return argTypes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, localName, variableExpr, function, mappedMethod, args);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != FunctionCallValueExpression.class) {
            return false;
        }

        FunctionCallValueExpression other = (FunctionCallValueExpression) obj;
        return Objects.equals(prefix, other.prefix)
            && Objects.equals(localName, other.localName)
            && Objects.equals(variableExpr, other.variableExpr)
            && Objects.equals(function, other.function)
            && Objects.equals(mappedMethod, other.mappedMethod)
            && Objects.equals(args, other.args);
    }
}
