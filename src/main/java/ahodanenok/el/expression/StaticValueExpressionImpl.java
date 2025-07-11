package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;

public class StaticValueExpressionImpl extends ValueExpressionBase {

    private final Object value;

    StaticValueExpressionImpl(Object value) {
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
        if (obj == null || !(obj instanceof StaticValueExpressionImpl)) {
            return false;
        }

        StaticValueExpressionImpl other = (StaticValueExpressionImpl) obj;
        return Objects.equals(value, other.value);
    }
}
