package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ahodanenok.el.expression.ExpressionFactoryStubs.ConversionRule;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class OrValueExpressionTest {

    @Test
    public void testOr_TrueFirst() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule(1, true, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new OrValueExpression(
            new StaticValueExpression(1),
            new StaticValueExpression(0));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testOr_TrueSecond() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule(0, false, Boolean.class),
            new ConversionRule(1, true, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new OrValueExpression(
            new StaticValueExpression(0),
            new StaticValueExpression(1));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testOr_FalseFalse() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ConversionRule(1, false, Boolean.class),
            new ConversionRule(2, false, Boolean.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new OrValueExpression(
            new StaticValueExpression(1),
            new StaticValueExpression(2));
        assertEquals(false, expr.getValue(context));
    }
}
