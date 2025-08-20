package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;
import ahodanenok.el.utils.StubValueExpression;
import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueReference;

public class AssignValueExpressionTest {

    @Test
    public void testAssign_LambdaArg() {
        var context = new StubELContext();
        var expr = new AssignValueExpression(
            new IdentifierValueExpression("a", null),
            new StaticValueExpression(1));
        context.enterLambdaScope(Map.of("a", 0));
        assertNull(expr.getValueReference(context));
        assertTrue(expr.isReadOnly(context));
        assertNull(expr.getExpectedType());
        PropertyNotWritableException ex = assertThrows(
            PropertyNotWritableException.class, () -> expr.getValue(context));
        assertEquals("Identifier 'a' is a lambda argument", ex.getMessage());
        context.exitLambdaScope();
    }

    @Test
    public void testAssign_Variable() {
        var context = new AssignELContext("b", "c", 1);
        var expr = new AssignValueExpression(
            new IdentifierValueExpression("a", new RefValueExpression("b", "c")),
            new StaticValueExpression(1));
        assertNull(expr.getValueReference(context));
        assertTrue(expr.isReadOnly(context));
        assertNull(expr.getExpectedType());
        assertEquals(Integer.valueOf(1), expr.getValue(context));
    }

    @Test
    public void testAssign_Other() {
        var context = new AssignELContext("response", "status", 500);
        var expr = new AssignValueExpression(
            new RefValueExpression("response", "status"),
            new StaticValueExpression(500));
        assertNull(expr.getValueReference(context));
        assertTrue(expr.isReadOnly(context));
        assertNull(expr.getExpectedType());
        assertEquals(Integer.valueOf(500), expr.getValue(context));
    }

    private static class RefValueExpression extends StubValueExpression {

        final Object base;
        final Object prop;

        RefValueExpression(Object base, Object prop) {
            this.base = base;
            this.prop = prop;
        }

        @Override
        public ValueReference getValueReference(ELContext context) {
            return new ValueReference(base, prop);
        }
    };

    private static class AssignELContext extends StubELContext {

        final Object expectedBase;
        final Object expectedProperty;
        final Object expectedValue;

        AssignELContext(Object expectedBase, Object expectedProperty, Object expectedValue) {
            this.expectedBase = expectedBase;
            this.expectedProperty = expectedProperty;
            this.expectedValue = expectedValue;
        }

        @Override
        public ELResolver getELResolver() {
            return new StubELResolver() {
                @Override
                public void setValue(ELContext context, Object base, Object property, Object value) {
                    assertEquals(expectedBase, base);
                    assertEquals(expectedProperty, property);
                    assertEquals(expectedValue, value);
                }
            };
        }
    }
}
