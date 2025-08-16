package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.el.ELContext;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.StandardELContext;
import jakarta.el.ValueReference;

public class IdentifierValueExpressionTest {

    @Test
    public void testLambdaArg() {
        StandardELContext context = new StandardELContext(ExpressionFactoryStubs.NONE);
        var expr = new IdentifierValueExpression("x", null);
        context.enterLambdaScope(Map.of("x", 1));
        assertEquals(true, expr.isReadOnly(context));
        assertEquals(Integer.valueOf(1), expr.getValue(context));
        assertNull(expr.getType(context));
        assertThrows(PropertyNotWritableException.class, () -> expr.setValue(context, 2));
        assertEquals(null, expr.getValueReference(context).getBase());
        assertEquals("x", expr.getValueReference(context).getProperty());
        context.exitLambdaScope();
    }

    @Test
    public void testVariable() {
        StandardELContext context = new StandardELContext(ExpressionFactoryStubs.NONE);
        var expr = new IdentifierValueExpression("x", new StaticValueExpression(10) {
            @Override
            public ValueReference getValueReference(ELContext context) {
                return new ValueReference("a", "b");
            }
        });
        assertEquals(true, expr.isReadOnly(context));
        assertEquals(Integer.valueOf(10), expr.getValue(context));
        assertNull(expr.getType(context));
        assertThrows(PropertyNotWritableException.class, () -> expr.setValue(context, 11));
        assertEquals("a", expr.getValueReference(context).getBase());
        assertEquals("b", expr.getValueReference(context).getProperty());
    }

    @Test
    public void testELResolver() {
        StandardELContext context = new StandardELContext(ExpressionFactoryStubs.NONE);
        var expr = new IdentifierValueExpression("x", null);
        assertThrows(PropertyNotFoundException.class, () -> expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertDoesNotThrow(() -> expr.setValue(context, 100));
        assertTrue(context.isPropertyResolved());
        assertEquals(Integer.valueOf(100), expr.getValue(context));
        assertTrue(context.isPropertyResolved());
        assertEquals(null, expr.getValueReference(context).getBase());
        assertEquals("x", expr.getValueReference(context).getProperty());
    }
}
