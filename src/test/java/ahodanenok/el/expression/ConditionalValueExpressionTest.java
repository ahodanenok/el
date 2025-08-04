package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ahodanenok.el.expression.ExpressionFactoryStubs.ConversionRule;
import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class ConditionalValueExpressionTest {

    @Test
    public void testConditional_ConditionTrue() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule("true", true, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ConditionalValueExpression(
            new StaticValueExpression("true"),
            new StaticValueExpression("a"),
            new ErrorValueExpression());
        assertEquals("a", expr.getValue(context));
    }

    @Test
    public void testConditional_ConditionFalse() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule("false", false, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ConditionalValueExpression(
            new StaticValueExpression("false"),
            new ErrorValueExpression(),
            new StaticValueExpression("b"));
        assertEquals("b", expr.getValue(context));
    }

    private static class ErrorValueExpression extends ValueExpressionBase {

        @Override
        public <T> T getValue(ELContext arg0) {
            throw new IllegalStateException("'getValue' should not be called");
        }

        @Override
        public boolean equals(Object arg0) {
            throw new IllegalStateException("'equals' should not be called");
        }

        @Override
        public int hashCode() {
            throw new IllegalStateException("'hashCode' should not be called");
        }
    };
}
