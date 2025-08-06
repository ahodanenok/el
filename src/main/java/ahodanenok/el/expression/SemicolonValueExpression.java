package ahodanenok.el.expression;

import java.util.List;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ValueExpression;

class SemicolonValueExpression extends ValueExpressionBase {

    final List<? extends ValueExpression> expressions;

    SemicolonValueExpression(List<? extends ValueExpression> expressions) {
        Objects.requireNonNull(expressions);
        if (expressions.isEmpty()) {
            throw new IllegalStateException("At least one expression is required");
        }

        this.expressions = expressions;
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
        for (int i = 0; i < expressions.size() - 1; i++) {
            expressions.get(i).getValue(context);
        }

        return expressions.get(expressions.size() - 1).getValue(context);
    }

    @Override
    public int hashCode() {
        return expressions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != SemicolonValueExpression.class) {
            return false;
        }

        SemicolonValueExpression other = (SemicolonValueExpression) obj;
        return expressions.equals(other.expressions);
    }
}
