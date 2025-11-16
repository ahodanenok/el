package ahodanenok.el.expression;

import java.util.List;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class CompositeValueExpression extends ValueExpressionBase {

    final List<ValueExpression> expressions;

    CompositeValueExpression(List<ValueExpression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public <T> T getValue(ELContext context) {
        try {
            return convertIfNecessary(context, getValueInternal(context));
        } catch (ELException e) {
            throw e;
        } catch (Exception e) {
            throw new ELException("Failed to calculate composite expression", e);
        }
    }

    private Object getValueInternal(ELContext context) {
        if (expressions.isEmpty()) {
            return "";
        }

        String result = context.convertToType(expressions.get(0).getValue(context), String.class);
        for (int i = 1; i < expressions.size(); i++) {
            result += context.convertToType(expressions.get(i).getValue(context), String.class);
        }

        return result;
    }

    @Override
    public int hashCode() {
        return expressions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != CompositeValueExpression.class) {
            return false;
        }

        CompositeValueExpression other = (CompositeValueExpression) obj;
        return expressions.equals(other.expressions);
    }
}
