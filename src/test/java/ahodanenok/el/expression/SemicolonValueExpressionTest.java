package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.el.StandardELContext;

public class SemicolonValueExpressionTest {

    @Test
    public void testEvaluate_One() {
        var context = new StandardELContext(ExpressionFactoryStubs.NONE);
        var expr = new SemicolonValueExpression(List.of(new StaticValueExpression(1)));
        assertEquals(Integer.valueOf(1), expr.getValue(context));
    }

    @Test
    public void testEvaluate_Multiple() {
        var context = new StandardELContext(ExpressionFactoryStubs.NONE);
        var expr = new SemicolonValueExpression(List.of(
            new StaticValueExpression(1), new StaticValueExpression(2), new StaticValueExpression(3)));
        assertEquals(Integer.valueOf(3), expr.getValue(context));
    }
}
