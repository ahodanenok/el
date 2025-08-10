package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jakarta.el.StandardELContext;

public class ParenthesisValueExpressionTest {

    @Test
    public void testEvaluate() {
        var context = new StandardELContext(ExpressionFactoryStubs.NONE);
        var expr = new ParenthesisValueExpression(new StaticValueExpression(1L));
        assertEquals(Long.valueOf(1), expr.getValue(context));
    }
}
