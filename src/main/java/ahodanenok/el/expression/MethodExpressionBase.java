package ahodanenok.el.expression;

import jakarta.el.MethodExpression;

public abstract class MethodExpressionBase extends MethodExpression {

    String expressionString;
    Class<?> expectedReturnType;
    Class<?>[] expectedParamTypes;

    @Override
    public String getExpressionString() {
        return expressionString;
    }

    @Override
    public boolean isLiteralText() {
        return false;
    }
}
