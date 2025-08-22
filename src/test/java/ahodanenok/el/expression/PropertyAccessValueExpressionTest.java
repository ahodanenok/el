package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;
import jakarta.el.ELContext;
import jakarta.el.PropertyNotFoundException;

public class PropertyAccessValueExpressionTest {

    @Test
    public void testAccess_BaseNull() {
        var context = new StubELContext();
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression(null),
            new StaticValueExpression("a"));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNull() {
        var context = new StubELContext();
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression(null));
        assertNull(expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getValueReference(context));
    }

    @Test
    public void testAccess_GetValue() {
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                return "c";
            }
        });
        assertEquals("c", expr.getValue(context));
    }

    @Test
    public void testAccess_IsReadOnly() {
        var expr = new PropertyAccessValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public boolean isReadOnly(ELContext context, Object base, Object property) {
                assertEquals("a", base);
                assertEquals("b", property);
                return false;
            }
        });
        assertEquals(false, expr.isReadOnly(context));
    }
}


