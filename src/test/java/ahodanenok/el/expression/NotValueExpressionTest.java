package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class NotValueExpressionTest {

    @Test
    public void testNot_True() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(Boolean.TRUE, Boolean.class);
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NotValueExpression(new StaticValueExpression("1"));
        assertEquals(Boolean.FALSE, expr.getValue(context));
    }

    @Test
    public void testNot_False() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(Boolean.FALSE, Boolean.class);
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NotValueExpression(new StaticValueExpression("1"));
        assertEquals(Boolean.TRUE, expr.getValue(context));
    }
}
