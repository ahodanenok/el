package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import jakarta.el.ValueReference;

public class BracketsAccessValueExpressionTest {

    @Test
    public void testAccess_BaseNull() {
        var context = new StubELContext();
        var expr = new BracketsAccessValueExpression(
            new StaticValueExpression(null),
            new StaticValueExpression("a"));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getType(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.setValue(context, 1));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNull() {
        var context = new StubELContext();
        var expr = new BracketsAccessValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression(null));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getType(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.setValue(context, 1));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNotResolved() {
        var expr = new BracketsAccessValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
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
        var expr = new BracketsAccessValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
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

    @Test
    public void testAccess_Writable() {
        var expr = new BracketsAccessValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"));

        var context = new StubELContext(new StubELResolver() {

            Object value = "c";

            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                return value;
            }

            @Override
            public Class<?> getType(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                return String.class;
            }

            @Override
            public boolean isReadOnly(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                return false;
            }

            @Override
            public void setValue(ELContext context, Object base, Object property, Object value) {
                assertEquals("a", base);
                assertEquals("b", property);
                context.setPropertyResolved(true);
                this.value = value;
            }
        });

        assertEquals("c", expr.getValue(context));
        assertEquals(String.class, expr.getType(context));
        assertFalse(expr.isReadOnly(context));
        assertDoesNotThrow(() -> expr.setValue(context, "d"));
        assertEquals("d", expr.getValue(context));
        assertEquals("a", expr.getValueReference(context).getBase());
        assertEquals("b", expr.getValueReference(context).getProperty());
    }
}
