package ahodanenok.el.expression;

import java.util.List;

import jakarta.el.ELContext;
import jakarta.el.LambdaExpression;
import jakarta.el.ValueExpression;

class LambdaValueExpression extends ValueExpressionBase {

    final List<String> params;
    final ValueExpression body;
    final LambdaExpression lambda;

    LambdaValueExpression(List<String> params, ValueExpression body) {
        this.params = params;
        this.body = body;
        this.lambda = new LambdaExpression(params, body);
    }

    @Override
    public <T> T getValue(ELContext context) {
        return convertIfNecessary(context, lambda);
    }

    @Override
    public int hashCode() {
        return lambda.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != LambdaValueExpression.class) {
            return false;
        }

        LambdaValueExpression other = (LambdaValueExpression) obj;
        return lambda.equals(other.lambda);
    }
}
