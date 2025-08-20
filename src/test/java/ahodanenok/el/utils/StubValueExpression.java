package ahodanenok.el.utils;

import ahodanenok.el.expression.ValueExpressionBase;
import jakarta.el.ELContext;

public class StubValueExpression extends ValueExpressionBase {

    @Override
    public <T> T getValue(ELContext context) {
        throw new IllegalStateException("'getValue' should not be called");
    }

    @Override
    public boolean equals(Object obj) {
        throw new IllegalStateException("'equals' should not be called");
    }

    @Override
    public int hashCode() {
        throw new IllegalStateException("'hashCode' should not be called");
    }
}
