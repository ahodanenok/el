package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

public class ParenthesisValueExpression extends ValueExpressionBase {

    final ValueExpression expr;

    ParenthesisValueExpression(ValueExpression expr) {
        this.expr = Objects.requireNonNull(expr);
    }

    @Override
    public <T> T getValue(ELContext context) {
        return expr.getValue(context);
    }

    @Override
    public int hashCode() {
        return expr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != ParenthesisValueExpression.class) {
            return false;
        }

        ParenthesisValueExpression other = (ParenthesisValueExpression) obj;
        return expr.equals(other.expr);
    }
}
