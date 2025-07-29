package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ValueExpression;

final class EqualValueExpression extends EqualityValueExpressionBase {

    EqualValueExpression(ValueExpression left, ValueExpression right) {
        super(left, right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != EqualValueExpression.class) {
            return false;
        }

        EqualValueExpression other = (EqualValueExpression) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
