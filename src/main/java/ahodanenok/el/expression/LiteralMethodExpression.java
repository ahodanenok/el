package ahodanenok.el.expression;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.MethodInfo;

public class LiteralMethodExpression extends MethodExpressionBase {

    private final String value;

    public LiteralMethodExpression(String value) {
        this.value = value;
    }

    @Override
    public MethodInfo getMethodInfo(ELContext context) {
        return null;
    }

    @Override
    public Object invoke(ELContext context, Object[] args) {
        if (expectedReturnType == null) {
            return value;
        } else if (expectedReturnType == Void.class || expectedReturnType == void.class) {
            throw new ELException("Unexpected return value for type void");
        } else {
            return context.convertToType(value, expectedReturnType);
        }
    }

    @Override
    public boolean isLiteralText() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != LiteralMethodExpression.class) {
            return false;
        }

        LiteralMethodExpression other = (LiteralMethodExpression) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
