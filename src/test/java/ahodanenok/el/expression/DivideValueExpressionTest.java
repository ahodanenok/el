package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class DivideValueExpressionTest {

    @Test
    public void testDivide_Nulls() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(null), new StaticValueExpression(null));
        assertEquals(Long.valueOf(0), expr.getValue(context));
    }

    @Test
    public void testDivide_Null_NotNull() {
        Object leftValue = null;
        Object rightValue = 1.5;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, null, Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(1.5), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
            assertEquals("Failed to divide", ex.getMessage());
    }

    @Test
    public void testDivide_NotNull_Null() {
        Object leftValue = 1.5;
        Object rightValue = null;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(1.5), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, null, Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
            assertEquals("Failed to divide", ex.getMessage());
    }

    @Test
    public void testDivide_BigInteger_X() {
        Object leftValue = BigInteger.valueOf(6);
        Object rightValue = 3.0;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(6.0), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(3.0), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(2.0), expr.getValue(context));
    }

    @Test
    public void testDivide_X_BigInteger() {
        Object leftValue = 6.0;
        Object rightValue = BigInteger.valueOf(3);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(6.0), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(3.0), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(2.0), expr.getValue(context));
    }

    @Test
    public void testDivide_BigDecimal_X() {
        Object leftValue = BigDecimal.valueOf(1.5);
        Object rightValue = 3;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(1.5), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(3.0), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(0.5), expr.getValue(context));
    }

    @Test
    public void testDivide_X_BigDecimal() {
        Object leftValue = 1.5;
        Object rightValue = BigDecimal.valueOf(3);
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, BigDecimal.valueOf(1.5), BigDecimal.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, BigDecimal.valueOf(3.0), BigDecimal.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(BigDecimal.valueOf(0.5), expr.getValue(context));
    }

    @Test
    public void testDivide_Other() {
        Object leftValue = "100";
        Object rightValue = 20;
        ExpressionFactory ef = ExpressionFactoryStubs.coerceToValue(
            new ExpressionFactoryStubs.ConversionRule(leftValue, Double.valueOf(100), Double.class),
            new ExpressionFactoryStubs.ConversionRule(rightValue, Double.valueOf(20), Double.class));
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression(leftValue), new StaticValueExpression(rightValue));
        assertEquals(Double.valueOf(5), expr.getValue(context));
    }

    @Test
    public void testAdd_NoConversion() {
        ExpressionFactory ef = ExpressionFactoryStubs.elException("Coersion failed");
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new DivideValueExpression(
            new StaticValueExpression("a"), new StaticValueExpression("b"));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
        assertEquals("Coersion failed", ex.getMessage());
    }
}
