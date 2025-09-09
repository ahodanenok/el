package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import ahodanenok.el.utils.StubELContext;
import ahodanenok.el.utils.StubELResolver;
import jakarta.el.ELContext;

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
}
