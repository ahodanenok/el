package ahodanenok.el.expression;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.MethodInfo;

public class StringMethodExpression extends MethodExpressionBase {

    private final String value;

    public StringMethodExpression(String value) {
        this.value = value;
    }

    @Override
    public MethodInfo getMethodInfo(ELContext arg0) {
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
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != StringMethodExpression.class) {
            return false;
        }

        StringMethodExpression other = (StringMethodExpression) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
