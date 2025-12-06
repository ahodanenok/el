package ahodanenok.el.expression;

import java.util.ArrayList;
import java.util.List;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

class ListValueExpression extends ValueExpressionBase {

    final List<ValueExpression> expressions;

    ListValueExpression(List<ValueExpression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public <T> T getValue(ELContext context) {
        List<Object> list = new ArrayList<>();
        for (ValueExpression expr : expressions) {
            list.add(expr.getValue(context));
        }

        return convertIfNecessary(context, list);
    }

    @Override
    public int hashCode() {
        return expressions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != ListValueExpression.class) {
            return false;
        }

        ListValueExpression other = (ListValueExpression) obj;
        return expressions.equals(other.expressions);
    }
}
