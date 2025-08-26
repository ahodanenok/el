package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;
import jakarta.el.ELContext;

public class PropertyCallValueExpressionTest {

    @Test
    public void testAccess_BaseNull() {
        var context = new StubELContext();
        var expr = new PropertyCallValueExpression(
            new StaticValueExpression(null),
            new StaticValueExpression("a"),
            List.of(new StaticValueExpression(1)));
        assertNull(expr.getValue(context));
        assertNull(expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNull() {
        var context = new StubELContext();
        var expr = new PropertyCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression(null),
            List.of(new StaticValueExpression(1)));
        assertNull(expr.getValue(context));
        assertNull(expr.getValueReference(context));
    }

    @Test
    public void testAccess_GetValue_NoArgs() {
        var expr = new PropertyCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"),
            List.of());

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertNull(paramTypes);
                assertArrayEquals(new Object[0], params);
                return "c";
            }
        });
        assertEquals("c", expr.getValue(context));
    }

    @Test
    public void testAccess_GetValue_OneArg() {
        var expr = new PropertyCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"),
            List.of(new StaticValueExpression(1)));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertNull(paramTypes);
                assertArrayEquals(new Object[] { 1 }, params);
                return "c";
            }
        });
        assertEquals("c", expr.getValue(context));
    }

    @Test
    public void testAccess_GetValue_MultipleArgs() {
        var expr = new PropertyCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"),
            List.of(
                new StaticValueExpression(1),
                new StaticValueExpression(2),
                new StaticValueExpression(3)));

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertNull(paramTypes);
                assertArrayEquals(new Object[] { 1, 2, 3 }, params);
                return "c";
            }
        });
        assertEquals("c", expr.getValue(context));
    }
}


