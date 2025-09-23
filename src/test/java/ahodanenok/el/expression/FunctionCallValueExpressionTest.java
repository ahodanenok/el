package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELException;

public class FunctionCallValueExpressionTest {

    public static String testStaticMethod(String a, long b) {
        return a + "-" + b;
    }

    @Test
    public void testCall_MappedMethod() throws Exception {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                assertNull(base);
                assertEquals("calculate", property);
                context.setPropertyResolved(false);
                return null;
            }
        });

        var expr = new FunctionCallValueExpression(
            null, "calculate", null,
            FunctionCallValueExpressionTest.class.getMethod("testStaticMethod", String.class, long.class),
            List.of(new StaticValueExpression("x"), new StaticValueExpression(10L)));
        assertEquals("x-10", expr.getValue(context));
    }

    @Test
    public void testCall_Lambda() {
        var context =  new StubELContext(new StubELResolver());

        LambdaValueExpression lambda = new LambdaValueExpression(
            List.of("a", "b"), new StaticValueExpression(123));

        var expr = new FunctionCallValueExpression(
            null, "getResult", lambda, null,
            List.of(new StaticValueExpression(1), new StaticValueExpression(2)));
        assertEquals(Integer.valueOf(123), expr.getValue(context));
    }

    @Test
    public void testCall_Constructor_NoArgs() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importClass(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$PublicConstructorTestClass");

        var expr = new FunctionCallValueExpression(
            null, "FunctionCallValueExpressionTest$PublicConstructorTestClass", null, null, List.of());
        PublicConstructorTestClass obj = assertInstanceOf(
            PublicConstructorTestClass.class, expr.getValue(context));
        assertNull(obj.s);
        assertNull(obj.n);
        assertNull(obj.b);
    }

    @Test
    public void testCall_Constructor_OneArg() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importClass(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$PublicConstructorTestClass");

        var expr = new FunctionCallValueExpression(
            null, "FunctionCallValueExpressionTest$PublicConstructorTestClass", null, null,
            List.of(new StaticValueExpression("hello")));
        PublicConstructorTestClass obj = assertInstanceOf(
            PublicConstructorTestClass.class, expr.getValue(context));
        assertEquals("hello", obj.s);
        assertNull(obj.n);
        assertNull(obj.b);
    }

    @Test
    public void testCall_Constructor_MultipleArgs() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importClass(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$PublicConstructorTestClass");

        var expr = new FunctionCallValueExpression(
            null, "FunctionCallValueExpressionTest$PublicConstructorTestClass", null, null,
            List.of(
                new StaticValueExpression("hello"),
                new StaticValueExpression(100),
                new StaticValueExpression(true)));
        PublicConstructorTestClass obj = assertInstanceOf(
            PublicConstructorTestClass.class, expr.getValue(context));
        assertEquals("hello", obj.s);
        assertEquals(100, obj.n);
        assertTrue(obj.b);
    }

    @Test
    public void testCall_Constructor_NotAccessible() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importClass(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$NotAccessibleConstructorTestClass");

        var expr = new FunctionCallValueExpression(
            null, "FunctionCallValueExpressionTest$NotAccessibleConstructorTestClass", null, null, List.of());
        assertEquals(
            "Function with name 'FunctionCallValueExpressionTest$NotAccessibleConstructorTestClass' wasn't resolved",
            assertThrows(ELException.class, () -> expr.getValue(context)).getMessage());
    }

    public static class PublicConstructorTestClass {

        private String s;
        private Integer n;
        private Boolean b;

        public PublicConstructorTestClass() { }

        public PublicConstructorTestClass(String s) {
            this.s = s;
        }

        public PublicConstructorTestClass(String s, Integer n, Boolean b) {
            this.s = s;
            this.n = n;
            this.b = b;
        }
    }

    public static class NotAccessibleConstructorTestClass {

        NotAccessibleConstructorTestClass() { }
    }

    @Test
    public void testCall_Method_NoArgs() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$MethodsTestClass.m1");

        var expr = new FunctionCallValueExpression(null, "m1", null, null, List.of());
        assertEquals("1", expr.getValue(context));
    }

    @Test
    public void testCall_Method_OneArg() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$MethodsTestClass.m2");

        var expr = new FunctionCallValueExpression(
            null, "m2", null, null, List.of(new StaticValueExpression(100)));
        assertEquals("n_100", expr.getValue(context));
    }

    @Test
    public void testCall_Method_MultipleArgs() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$MethodsTestClass.m3");

        var expr = new FunctionCallValueExpression(
            null, "m3", null, null, List.of(
                new StaticValueExpression("test"),
                new StaticValueExpression(100),
                new StaticValueExpression(true)));
        assertEquals("test_100_true", expr.getValue(context));
    }

    @Test
    public void testCall_Method_NotAccessible() {
        var context =  new StubELContext(new StubELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                context.setPropertyResolved(false);
                return null;
            }
        });
        context.getImportHandler().importStatic(
            "ahodanenok.el.expression.FunctionCallValueExpressionTest$MethodsTestClass.m4");

        var expr = new FunctionCallValueExpression(null, "m4", null, null, List.of());
        assertEquals(
            "Function with name 'm4' wasn't resolved",
            assertThrows(ELException.class, () -> expr.getValue(context)).getMessage());
    }

    public static class MethodsTestClass {

        public static String m1() {
            return "1";
        }

        public static String m2(Integer n) {
            return "n_" + n;
        }

        public static String m3(String s, Integer n, Boolean b) {
            return s + "_" + n + "_" + b;
        }

        static String m4() {
            return "";
        }
    }
}
