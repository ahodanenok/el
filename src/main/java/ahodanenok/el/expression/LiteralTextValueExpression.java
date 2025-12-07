package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;

class LiteralTextValueExpression extends ValueExpressionBase {

    final String text;

    LiteralTextValueExpression(String text) {
        this.text = text;
    }

    @Override
    public <T> T getValue(ELContext context) {
        return convertIfNecessary(context, text);
    }

    @Override
    public boolean isLiteralText() {
        return true;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LiteralTextValueExpression)) {
            return false;
        }

        LiteralTextValueExpression other = (LiteralTextValueExpression) obj;
        return text.equals(other.text);
    }
}
