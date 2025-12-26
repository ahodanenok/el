package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELException;
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
        var expr = new IdentifierValueExpression("x", new StaticValueExpression(10));
        assertEquals(true, expr.isReadOnly(context));
        assertEquals(Integer.valueOf(10), expr.getValue(context));
        assertNull(expr.getType(context));
        assertThrows(PropertyNotWritableException.class, () -> expr.setValue(context, 11));
        assertNull(expr.getValueReference(context));
    }

    @Test
    public void testELResolver() {
        ELContext context = new StubELContext(new StubELResolver() {

            Object value = 1;

            @Override
            public Class<?> getType(ELContext context, Object base, Object property) {
                if (base == null && property.equals("x")) {
                    context.setPropertyResolved(true);
                    return Integer.class;
                } else {
                    context.setPropertyResolved(false);
                    return null;
                }
            }

            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                if (base == null && property.equals("x")) {
                    context.setPropertyResolved(true);
                    return value;
                } else {
                    context.setPropertyResolved(false);
                    return null;
                }
            }

            @Override
            public boolean isReadOnly(ELContext context, Object base, Object property) {
                if (base == null && property.equals("x")) {
                    context.setPropertyResolved(true);
                    return false;
                } else {
                    context.setPropertyResolved(false);
                    return true;
                }
            }

            @Override
            public void setValue(ELContext context, Object base, Object property, Object value) {
                if (base == null && property.equals("x")) {
                    context.setPropertyResolved(true);
                    this.value = value;
                } else {
                    context.setPropertyResolved(false);
                }
            }
        });
        var expr = new IdentifierValueExpression("x", null);
        assertEquals(Integer.valueOf(1), expr.getValue(context));
        assertFalse(expr.isReadOnly(context));
        assertEquals(Integer.class, expr.getType(context));
        assertDoesNotThrow(() -> expr.setValue(context, 100));
        assertEquals(Integer.valueOf(100), expr.getValue(context));
        assertEquals(null, expr.getValueReference(context).getBase());
        assertEquals("x", expr.getValueReference(context).getProperty());
    }

    @Test
    public void testStaticField() throws Exception {
        StubELContext context = new StubELContext(new StaticsELResolver());
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.IdentifierValueExpressionTest$Statics.f1");
        var expr = new IdentifierValueExpression("f1", null);
        assertEquals("100", expr.getValue(context));
        assertTrue(expr.isReadOnly(context));
        assertNull(expr.getType(context));
        assertThrows(PropertyNotWritableException.class, () -> expr.setValue(context, "123"));
        assertEquals("100", expr.getValue(context));
    }

    @Test
    public void testStaticFinalField() throws Exception {
        StubELContext context = new StubELContext(new StaticsELResolver());
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.IdentifierValueExpressionTest$Statics.f2");
        var expr = new IdentifierValueExpression("f2", null);
        assertEquals("200", expr.getValue(context));
        assertTrue(expr.isReadOnly(context));
        assertNull(expr.getType(context));
        assertThrows(PropertyNotWritableException.class, () -> expr.setValue(context, "234"));
        assertEquals("200", expr.getValue(context));
    }

    @Test
    public void testStaticNonPublicField() throws Exception {
        StubELContext context = new StubELContext(new StaticsELResolver());
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.IdentifierValueExpressionTest$Statics.f3");
        var expr = new IdentifierValueExpression("f3", null);
        assertThrows(PropertyNotFoundException.class, () -> expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getType(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.setValue(context, "345"));
    }

    @Test
    public void testStaticNonStaticField() throws Exception {
        StubELContext context = new StubELContext(new StaticsELResolver());
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.IdentifierValueExpressionTest$Statics.f4");
        var expr = new IdentifierValueExpression("f4", null);
        assertThrows(PropertyNotFoundException.class, () -> expr.getValue(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.isReadOnly(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.getType(context));
        assertThrows(PropertyNotFoundException.class, () -> expr.setValue(context, "456"));
    }

    public static class Statics {

        public static String f1 = "100";
        public static final String f2 = "200";
        static String f3 = "300";
        public String f4 = "400";
    }

    public static class StaticsELResolver extends StubELResolver {

        @Override
        public Class<?> getType(ELContext context, Object base, Object property) {
            context.setPropertyResolved(false);
            return null;
        }

        @Override
        public Object getValue(ELContext context, Object base, Object property) {
            context.setPropertyResolved(false);
            return null;
        }

        @Override
        public boolean isReadOnly(ELContext context, Object base, Object property) {
            context.setPropertyResolved(false);
            return true;
        }

        @Override
        public void setValue(ELContext context, Object base, Object property, Object value) {
            context.setPropertyResolved(false);
        }
    }
}
