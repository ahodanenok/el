package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class SubtractValueExpressionTest {

    @Test
    public void testSubtract_Nulls() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(null), new StaticValueExpression(null));
        assertEquals(Long.valueOf(0), expr.getValue(context));
    }

    @Test
    public void testSubtract_BigDecimal_X() {
        Object leftValue = BigDecimal.valueOf(1.5);
        Object rightValue = Integer.valueOf(2);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(1.5), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(2), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(-0.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_X_BigDecimal() {
        Object leftValue = Integer.valueOf(2);
        Object rightValue = BigDecimal.valueOf(1.5);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(2), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(1.5), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(0.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_Float_BigInteger() {
        Object leftValue = Float.valueOf(0.5f);
        Object rightValue = BigInteger.TEN;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(0.5), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(10), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(-9.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_BigInteger_Float() {
        Object leftValue = BigInteger.TEN;
        Object rightValue = Float.valueOf(0.5f);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(10), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(0.5f), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(9.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_Float_X() {
        Object leftValue = Float.valueOf(2.5f);
        Object rightValue = Integer.valueOf(4);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(2.5), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(4), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(-1.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_X_Float() {
        Object leftValue = Integer.valueOf(4);
        Object rightValue = Float.valueOf(2.5f);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(4), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(2.5), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(1.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_Double_BigInteger() {
        Object leftValue = Double.valueOf(0.5f);
        Object rightValue = BigInteger.TEN;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(0.5), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(10), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(-9.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_BigInteger_Double() {
        Object leftValue = BigInteger.TEN;
        Object rightValue = Double.valueOf(0.5f);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(10), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(0.5f), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(9.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_Double_X() {
        Object leftValue = Double.valueOf(2.5f);
        Object rightValue = Integer.valueOf(4);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(2.5), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(4), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(-1.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_X_Double() {
        Object leftValue = Integer.valueOf(4);
        Object rightValue = Double.valueOf(2.5f);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(4), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(2.5), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(1.5), expr.getValue(context));
    }

    @Test
    public void testSubtract_String_BigInteger() {
        Object leftValue = "2.7";
        Object rightValue = BigInteger.TEN;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(2.7), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(10), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(-7.3), expr.getValue(context));
    }

    @Test
    public void testSubtract_BigInteger_String() {
        Object leftValue = BigInteger.TEN;
        Object rightValue = "2.7";
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(10), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(2.7), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(7.3), expr.getValue(context));
    }

    @Test
    public void testSubtract_String_X() {
        Object leftValue = "5.2";
        Object rightValue = true;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(5.2), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(2), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(3.2), expr.getValue(context));
    }

    @Test
    public void testSubtract_X_String() {
        Object leftValue = true;
        Object rightValue = "5.2";
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(2), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(5.2), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(-3.2), expr.getValue(context));
    }

    @Test
    public void testSubtract_BigInteger_X() {
        Object leftValue = BigInteger.TEN;
        Object rightValue = "3";
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigInteger.TEN, BigInteger.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigInteger.valueOf(3), BigInteger.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigInteger.valueOf(7), expr.getValue(context));
    }

    @Test
    public void testSubtract_X_BigInteger() {
        Object leftValue = "3";
        Object rightValue = BigInteger.TEN;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigInteger.valueOf(3), BigInteger.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigInteger.valueOf(10), BigInteger.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigInteger.valueOf(-7), expr.getValue(context));
    }

    @Test
    public void testSubtract_Other() {
        Object leftValue = Integer.valueOf(2);
        Object rightValue = Integer.valueOf(5);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Long.valueOf(2), Long.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Long.valueOf(5), Long.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Long.valueOf(-3), expr.getValue(context));
    }

    @Test
    public void testSubtract_NoConversion() {
        ExpressionFactory ef = ExpressionFactoryStubs.elException("Coersion failed");
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new SubtractValueExpression(
            new StaticValueExpression("a"), new StaticValueExpression("b"));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
        assertEquals("Coersion failed", ex.getMessage());
    }
}
