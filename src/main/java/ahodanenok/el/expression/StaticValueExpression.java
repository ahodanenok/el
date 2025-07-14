package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;

class StaticValueExpression extends ValueExpressionBase {

    final Object value;

    StaticValueExpression(Object value) {
        this.value = value;
    }

    @Override
    public <T> T getValue(ELContext context) {
        return convertIfNecessary(context, value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StaticValueExpression)) {
            return false;
        }

        StaticValueExpression other = (StaticValueExpression) obj;
        return Objects.equals(value, other.value);
    }
}
