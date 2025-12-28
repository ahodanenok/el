package ahodanenok.el.expression;

import java.util.List;
import jakarta.el.ELContext;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueReference;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BracketsCallValueExpressionTest {

    @Test
    public void testAccess_BaseNull() {
        var context = new StubELContext();
        var expr = new BracketsCallValueExpression(
            new StaticValueExpression(null),
            new StaticValueExpression("a"),
            List.of(new StaticValueExpression(1)));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getType(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.setValue(context, 1));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNull() {
        var context = new StubELContext();
        var expr = new BracketsCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression(null),
            List.of(new StaticValueExpression(1)));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getType(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.setValue(context, 1));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNotResolved() {
        var expr = new BracketsCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"),
            List.of(new StaticValueExpression(1)));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertArrayEquals(new Integer[] { 1 }, params);
                context.setPropertyResolved(false);
                return null;
            }

            @Override
            public Class<?> getType(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(false);
                return null;
            }

            @Override
            public boolean isReadOnly(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(false);
                return true;
            }

            @Override
            public void setValue(ELContext context, Object base, Object property, Object value) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(false);
            }
        });

        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getType(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.setValue(context, 1));
        assertEquals("a", expr.getValueReference(context).getBase());
        assertEquals("b", expr.getValueReference(context).getProperty());
    }

    @Test
    public void testAccess_NonWritable() {
        var expr = new BracketsCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"),
            List.of(new StaticValueExpression(1)));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertArrayEquals(new Integer[] { 1 }, params);
                context.setPropertyResolved(true);
                return "c";
            }

            @Override
            public Class<?> getType(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                return null;
            }

            @Override
            public boolean isReadOnly(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                return true;
            }

            @Override
            public void setValue(ELContext context, Object base, Object property, Object value) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                throw new PropertyNotWritableException();
            }
        });

        assertEquals("c", expr.getValue(context));
        assertNull(expr.getType(context));
        assertTrue(expr.isReadOnly(context));
        assertThrows(PropertyNotWritableException.class, () -> expr.setValue(context, "d"));
        assertEquals("a", expr.getValueReference(context).getBase());
        assertEquals("b", expr.getValueReference(context).getProperty());
    }
}
