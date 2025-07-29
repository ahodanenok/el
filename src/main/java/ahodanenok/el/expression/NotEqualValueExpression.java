package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

final class NotEqualValueExpression extends EqualityValueExpressionBase {

    NotEqualValueExpression(ValueExpression left, ValueExpression right) {
        super(left, right);
    }

    @Override
    protected Boolean getValueInternal(ELContext context) {
        return !super.getValueInternal(context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != NotEqualValueExpression.class) {
            return false;
        }

        NotEqualValueExpression other = (NotEqualValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
