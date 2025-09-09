package ahodanenok.el.expression;

import java.lang.reflect.Method;
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
    final Method method;
    final List<ValueExpressionBase> args;

    FunctionCallValueExpression(String prefix, String localName, ValueExpression variableExpr, Method method, List<ValueExpressionBase> args) {
        this.prefix = prefix;
        this.localName = localName;
        this.variableExpr = variableExpr;
        this.function = null;
        this.method = method;
        this.args = args;
    }

    FunctionCallValueExpression(ValueExpressionBase function, List<ValueExpressionBase> args) {
        this.prefix = null;
        this.localName = null;
        this.variableExpr = null;
        this.function = function;
        this.method = null;
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

    private Object getValueInternal(ELContext context) throws Exception {
        Object functionObj = null;
        if (function != null) {
            functionObj = this.function;
        } else if (prefix == null && method == null) {
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
            return lambda.invoke(context, evaluateArgs(context));
        } else if (method != null) {
            return method.invoke(null, evaluateArgs(context));
        } else if (functionObj instanceof String name) {
            // if (name of imported class) {
                // todo: invoke constructor
            // }
            // else if (name of imported static method) {
                // todo: invoke static method
            // }

            throw new ELException("Function with name '%s' not found".formatted(name));
        } else {
            if (functionObj != null) {
                throw new ELException("Object of type '%s' can't be invoked as function".formatted(functionObj.getClass().getName()));
            } else {
                throw new ELException("Null can't be invoked as function");
            }
        }
    }

    private Object[] evaluateArgs(ELContext context) {
        Object[] values = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            values[i] = args.get(i).getValue(context);
        }

        return values;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, localName, variableExpr, function, method, args);
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
            && Objects.equals(method, other.method)
            && Objects.equals(args, other.args);
    }
}
