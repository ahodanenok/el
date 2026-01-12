package ahodanenok.el.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.MethodInfo;
import jakarta.el.MethodNotFoundException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

class BracketsMethodExpression extends MethodExpressionBase {

    final ValueExpression baseExpr;
    final ValueExpression methodExpr;
    final List<ValueExpressionBase> argExprs;

    BracketsMethodExpression(
            ValueExpression baseExpr,
            ValueExpression methodExpr,
            List<ValueExpressionBase> argExprs) {
        this.baseExpr = baseExpr;
        this.methodExpr = methodExpr;
        this.argExprs = argExprs;
    }

    @Override
    public boolean isArgumentsProvided() {
        return argExprs != null;
    }

    @Override
    public MethodInfo getMethodInfo(ELContext context) {
        Object base = baseExpr.getValue(context);
        if (base == null) {
            throw new PropertyNotFoundException("Trying to dereference null value");
        }

        Object methodNameValue = methodExpr.getValue(context);
        if (methodNameValue == null) {
            throw new PropertyNotFoundException("Trying to dereference null value");
        }

        String methodName = context.convertToType(methodNameValue, String.class);
        Method method;
        try {
            if (argExprs == null) {
                Objects.requireNonNull(expectedParamTypes);
                method = base.getClass().getDeclaredMethod(methodName, expectedParamTypes);
            } else if (expectedParamTypes != null) {
                method = base.getClass().getDeclaredMethod(methodName, expectedParamTypes);
            } else {
                method = MethodExpressionUtils.findMethod(
                    base, methodName, ExpressionUtils.evaluateArgs(context, argExprs));
            }
        } catch (NoSuchMethodException e) {
            throw new MethodNotFoundException(methodName, e);
        }

        if (method == null) {
            throw new MethodNotFoundException(methodName);
        }

        return new MethodInfo(methodName, method.getReturnType(), method.getParameterTypes());
    }

    @Override
    public Object invoke(ELContext context, Object[] args) {
        Object base = baseExpr.getValue(context);
        if (base == null) {
            throw new PropertyNotFoundException("Trying to dereference null value");
        }


        Object methodNameValue = methodExpr.getValue(context);
        if (methodNameValue == null) {
            throw new PropertyNotFoundException("Trying to dereference null value");
        }

        String methodName = context.convertToType(methodNameValue, String.class);
        if (argExprs == null) {
            Objects.requireNonNull(expectedParamTypes);
            Method method;
            try {
                method = base.getClass().getDeclaredMethod(
                    methodName,
                    expectedParamTypes);
            } catch (NoSuchMethodException e) {
                throw new MethodNotFoundException(methodName);
            }

            try {
                return method.invoke(base, args);
            } catch (Exception e) {
                throw new ELException(e);
            }
        } else {
            return context.getELResolver().invoke(
                context,
                base,
                methodName,
                expectedParamTypes,
                ExpressionUtils.evaluateArgs(context, argExprs));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseExpr, methodExpr, argExprs);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != BracketsMethodExpression.class) {
            return false;
        }

        BracketsMethodExpression other = (BracketsMethodExpression) obj;
        return baseExpr.equals(other.baseExpr)
            && methodExpr.equals(other.methodExpr)
            && Objects.equals(argExprs, other.argExprs);
    }
}
