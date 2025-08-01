package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ahodanenok.el.expression.ExpressionFactoryStubs.ConversionRule;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class AndValueExpressionTest {


    @Test
    public void testAnd_TrueTrue() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule(1, true, Boolean.class),
            new ConversionRule(2, true, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new AndValueExpression(
            new StaticValueExpression(1),
            new StaticValueExpression(2));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testAnd_FalseFirst() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule(0, false, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new AndValueExpression(
            new StaticValueExpression(0),
            new StaticValueExpression(1));
        assertEquals(false, expr.getValue(context));
    }

    @Test
    public void testAnd_FalseSecond() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule(1, true, Boolean.class),
            new ConversionRule(0, false, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new AndValueExpression(
            new StaticValueExpression(1),
            new StaticValueExpression(0));
        assertEquals(false, expr.getValue(context));
    }
}
