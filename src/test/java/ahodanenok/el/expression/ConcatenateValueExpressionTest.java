package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class ConcatenateValueExpressionTest {

    @Test
    public void testConcatenate_Success() {
        Object leftValue = true;
        Object rightValue = 500;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, "true", String.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, "500", String.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ConcatenateValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals("true500", expr.getValue(context));
    }

    @Test
    public void testConcatenate_Error() {
        ExpressionFactory ef = ExpressionFactoryStubs.elException("Coersion failed");
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ConcatenateValueExpression(
            new StaticValueExpression(1), new StaticValueExpression(2));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
        assertEquals("Coersion failed", ex.getMessage());
    }
}
