package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class NegateValueExpressionTest {

    @Test
    public void testNegate_Null() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(null));
        assertEquals(Long.valueOf(0), expr.getValue(context));
    }

    @Test
    public void testNegate_BigDecimal() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(BigDecimal.valueOf(123.45)));
        assertEquals(BigDecimal.valueOf(-123.45), expr.getValue(context));
    }

    @Test
    public void testNegate_BigInteger() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(BigInteger.valueOf(100)));
        assertEquals(BigInteger.valueOf(-100), expr.getValue(context));
    }

    @Test
    public void testNegate_String_Double() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(Double.valueOf(1.5), Double.class);
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression("1.5"));
        assertEquals(Double.valueOf(-1.5), expr.getValue(context));
    }

    @Test
    public void testNegate_String_Long() {
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(Long.valueOf(10), Long.class);
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression("10"));
        assertEquals(Long.valueOf(-10), expr.getValue(context));
    }

    @Test
    public void testNegate_Byte() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(Byte.valueOf((byte) 1)));
        assertEquals(Byte.valueOf((byte) -1), expr.getValue(context));
    }

    @Test
    public void testNegate_Short() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(Short.valueOf((short) 1)));
        assertEquals(Short.valueOf((short) -1), expr.getValue(context));
    }

    @Test
    public void testNegate_Integer() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(Integer.valueOf(1)));
        assertEquals(Integer.valueOf(-1), expr.getValue(context));
    }

    @Test
    public void testNegate_Long() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(Long.valueOf(1)));
        assertEquals(Long.valueOf(-1), expr.getValue(context));
    }

    @Test
    public void testNegate_Float() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(Float.valueOf(1)));
        assertEquals(Float.valueOf(-1), expr.getValue(context));
    }

    @Test
    public void testNegate_Double() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(Double.valueOf(1)));
        assertEquals(Double.valueOf(-1), expr.getValue(context));
    }

    @Test
    public void testNegate_Unsupported() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new NegateValueExpression(new StaticValueExpression(true));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
        assertEquals("Numeric negation is not defined for type 'java.lang.Boolean'", ex.getMessage());
    }
}
