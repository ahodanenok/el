package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class ModuloValueExpressionTest {

    @Test
    public void testModulo_Nulls() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(null), new StaticValueExpression(null));
        assertEquals(Long.valueOf(0), expr.getValue(context));
    }

    @Test
    public void testModulo_Null_NotNull() {
        Object leftValue = null;
        Object rightValue = 2;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, null, Long.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Long.valueOf(2), Long.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
            assertEquals("Modulo operator failed", ex.getMessage());
    }

    @Test
    public void testModulo_NotNull_Null() {
        Object leftValue = 2;
        Object rightValue = null;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Long.valueOf(2), Long.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, null, Long.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
            assertEquals("Modulo operator failed", ex.getMessage());
    }

    @Test
    public void testModulo_BigDecimal_X() {
        Object leftValue = BigDecimal.valueOf(13);
        Object rightValue = 6;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(13.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(6.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(1.0), expr.getValue(context));
    }

    @Test
    public void testModulo_X_BigDecimal() {
        Object leftValue = 13;
        Object rightValue = BigDecimal.valueOf(6);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(13.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(6.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(1.0), expr.getValue(context));
    }

    @Test
    public void testModulo_Float_X() {
        Object leftValue = 21f;
        Object rightValue = 6;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(21.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(6.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(3.0), expr.getValue(context));
    }

    @Test
    public void testModulo_X_Float() {
        Object leftValue = 21;
        Object rightValue = 6f;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(21.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(6.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(3.0), expr.getValue(context));
    }

    @Test
    public void testModulo_Double_X() {
        Object leftValue = 17.0;
        Object rightValue = 5;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(17.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(5.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(2.0), expr.getValue(context));
    }

    @Test
    public void testModulo_X_Double() {
        Object leftValue = 17;
        Object rightValue = 5.0;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(17.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(5.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(2.0), expr.getValue(context));
    }

    @Test
    public void testModulo_String_X() {
        Object leftValue = "14.0";
        Object rightValue = 5;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(14.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(5.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(4.0), expr.getValue(context));
    }

    @Test
    public void testModulo_X_String() {
        Object leftValue = 14;
        Object rightValue = "5.0";
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(14.0), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(5.0), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(4.0), expr.getValue(context));
    }

    @Test
    public void testModulo_BigInteger_X() {
        Object leftValue = BigInteger.valueOf(8);
        Object rightValue = 3;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigInteger.valueOf(8), BigInteger.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigInteger.valueOf(3), BigInteger.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigInteger.valueOf(2), expr.getValue(context));
    }

    @Test
    public void testModulo_X_BigInteger() {
        Object leftValue = 8;
        Object rightValue = BigInteger.valueOf(3);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigInteger.valueOf(8), BigInteger.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigInteger.valueOf(3), BigInteger.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigInteger.valueOf(2), expr.getValue(context));
    }

    @Test
    public void testModulo_Other() {
        Object leftValue = (byte) 11;
        Object rightValue = (short) 4;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Long.valueOf(11), Long.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Long.valueOf(4), Long.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Long.valueOf(3), expr.getValue(context));
    }

    @Test
    public void testModulo_NoConversion() {
        ExpressionFactory ef = ExpressionFactoryStubs.elException("Coersion failed");
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new ModuloValueExpression(
            new StaticValueExpression("a"), new StaticValueExpression("b"));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
        assertEquals("Coersion failed", ex.getMessage());
    }
}
