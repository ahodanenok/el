package ahodanenok.el.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

class SetValueExpression extends ValueExpressionBase {

    final List<ValueExpressionBase> expressions;

    SetValueExpression(List<ValueExpressionBase> expressions) {
        this.expressions = expressions;
    }

    @Override
    public <T> T getValue(ELContext context) {
        Set<Object> set = new HashSet<>();
        for (ValueExpressionBase expr : expressions) {
            set.add(expr.getValue(context));
        }

        return convertIfNecessary(context, set);
    }

    @Override
    public int hashCode() {
        return expressions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != SetValueExpression.class) {
            return false;
        }

        SetValueExpression other = (SetValueExpression) obj;
        return expressions.equals(other.expressions);
    }
}
