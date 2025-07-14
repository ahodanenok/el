package ahodanenok.el.expression;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

class NotValueExpression extends ValueExpressionBase {

    final ValueExpression expr;

    NotValueExpression(ValueExpression expr) {
        this.expr = expr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ELContext context) {
        Boolean b = (Boolean) context.convertToType(
            expr.getValue(context), Boolean.class);
        return (T) Boolean.valueOf(!b);
    }

    @Override
    public int hashCode() {
        return expr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != NotValueExpression.class) {
            return false;
        }

        NotValueExpression other = (NotValueExpression) obj;
        return expr.equals(other.expr);
    }
}
