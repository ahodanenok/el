package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;
import jakarta.el.ELContext;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;

public class PropertyAccessValueExpressionTest {

    @Test
    public void testAccess_BaseNull() {
        var context = new StubELContext();
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression(null),
            "a",
            new StaticValueExpression("a"));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNull() {
        var context = new StubELContext();
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression("a"),
            null,
            new StaticValueExpression(null));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_GetValue() {
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression("a"),
            "b",
            new StaticValueExpression("b"));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                return "c";
            }
        });
        assertEquals("c", expr.getValue(context));
    }

    @Test
    public void testAccess_IsReadOnly() {
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression("a"),
            "b",
            new StaticValueExpression("b"));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public boolean isReadOnly(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                return false;
            }
        });
        assertEquals(false, expr.isReadOnly(context));
    }

    @Test
    public void testAccessStaticField() {
        var expr = new PropertyAccessValueExpression(
            new IdentifierValueExpression("PropertyAccessValueExpressionTest$StaticsTest", null),
            "f",
            new StaticValueExpression("f"));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return "x";
            }

            @Override
            public boolean isReadOnly(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return true;
            }
        });
        context.getImportHandler().importClass(
            "ahodanenok.el.expression.PropertyAccessValueExpressionTest$StaticsTest");

        assertEquals("hello", expr.getValue(context));
        assertTrue(expr.isReadOnly(context));
        assertNull(expr.getType(context));
        assertThrows(PropertyNotWritableException.class, () -> expr.setValue(context, "test"));
    }

    public static class StaticsTest {

        public static String f = "hello";
    }
}


