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
            List.of(new StaticValueExpression(1)),
            false);
        assertNull(expr.getValue(context));
        assertNull(expr.getValueReference(context));
    }

    @Test
    public void testAccess_PropertyNull() {
        var context = new StubELContext();
        var expr = new PropertyCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression(null),
            List.of(new StaticValueExpression(1)),
            false);
        assertNull(expr.getValue(context));
        assertNull(expr.getValueReference(context));
    }

    @Test
    public void testAccess_GetValue_NoArgs() {
        var expr = new PropertyCallValueExpression(
            new StaticValueExpression("a"),
            new StaticValueExpression("b"),
            List.of(),
            false);

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertNull(paramTypes);
                assertArrayEquals(new Object[0], params);
                context.setPropertyResolved(true);
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
            List.of(new StaticValueExpression(1)),
            false);

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertNull(paramTypes);
                assertArrayEquals(new Object[] { 1 }, params);
                context.setPropertyResolved(true);
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
                new StaticValueExpression(3)),
            false);

        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                assertEquals("a", base);
                assertEquals("b", method);
                assertNull(paramTypes);
                assertArrayEquals(new Object[] { 1, 2, 3 }, params);
                context.setPropertyResolved(true);
                return "c";
            }
        });
        assertEquals("c", expr.getValue(context));
    }

    @Test
    public void testCall_StaticMethod() {
        var expr = new PropertyCallValueExpression(
            new IdentifierValueExpression("PropertyCallValueExpressionTest$TestMethod", null),
            new StaticValueExpression("getString"),
            List.of(new StaticValueExpression("world")),
            true);
        
        var context = new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }

            @Override
            public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importClass(
            "ahodanenok.el.expression.PropertyCallValueExpressionTest$TestMethod");

        assertEquals("helloworld", expr.getValue(context));
    }

    public static class TestMethod {

        public static String getString(String arg) {
            return "hello" + arg;
        }
    }
}


