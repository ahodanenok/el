package ahodanenok.el.expression;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.MethodExpression;
import jakarta.el.MethodInfo;

class IdentifierMethodExpression extends MethodExpressionBase {

    private final IdentifierValueExpression idExpr;

    public IdentifierMethodExpression(IdentifierValueExpression idExpr) {
        this.idExpr = idExpr;
    }

    @Override
    public MethodInfo getMethodInfo(ELContext context) {
        Object value = idExpr.getValue(context);
        if (value instanceof MethodExpression methodExpr) {
            return methodExpr.getMethodInfo(context);
        } else {
            throw new ELException("Identifier '%s' doesn't refer to a method".formatted(idExpr.name));
        }
    }

    @Override
    public Object invoke(ELContext context, Object[] args) {
        Object value = idExpr.getValue(context);
        if (value instanceof MethodExpression methodExpr) {
            Object methodResult = methodExpr.invoke(context, null);
            if (expectedReturnType != null) {
                return context.convertToType(methodResult, expectedReturnType);
            } else {
                return methodResult;
            }
        } else {
            throw new ELException("Identifier '%s' doesn't refer to a method".formatted(idExpr.name));
        }
    }

    @Override
    public int hashCode() {
        return idExpr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != IdentifierMethodExpression.class) {
            return false;
        }

        IdentifierMethodExpression other = (IdentifierMethodExpression) obj;
        return idExpr.equals(other.idExpr);
    }
}
