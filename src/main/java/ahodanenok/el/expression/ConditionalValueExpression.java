package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class ConditionalValueExpression extends ValueExpressionBase {

    final ValueExpression condition;
    final ValueExpression onTrue;
    final ValueExpression onFalse;

    ConditionalValueExpression(ValueExpression condition, ValueExpression onTrue, ValueExpression onFalse) {
        this.condition = condition;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ELContext context) {
        try {
            return (T) getValueInternal(context);
        } catch (ELException e) {
            throw e;
        } catch (Exception e) {
            throw new ELException("Failed to evalulate expression", e);
        }
    }

    private Object getValueInternal(ELContext context) {
        Object conditionValue = condition.getValue(context);
        if (context.convertToType(conditionValue, Boolean.class)) {
            return onTrue.getValue(context);
        } else {
            return onFalse.getValue(context);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, onTrue, onFalse);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != ConditionalValueExpression.class) {
            return false;
        }

        ConditionalValueExpression other = (ConditionalValueExpression) obj;
        return condition.equals(other.condition)
            && onTrue.equals(other.onTrue)
            && onFalse.equals(other.onFalse);
    }
}
