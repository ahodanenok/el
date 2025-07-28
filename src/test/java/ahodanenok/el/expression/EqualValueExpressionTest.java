package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class EqualValueExpressionTest {

    @TestFactory
    public List<DynamicTest> testCompare() {
        return List.of(
            dynamicTest("same", () -> check(true, "a", "a", null, null, null)),
            dynamicTest("null_x", () -> check(false, null, "a", null, null, null)),
            dynamicTest("x_null", () -> check(false, "a", null, null, null, null)),
            dynamicTest("bigdecimal_x_true", () -> check(true, BigDecimal.valueOf(5), 5, BigDecimal.valueOf(5), BigDecimal.valueOf(5), BigDecimal.class)),
            dynamicTest("bigdecimal_x_false", () -> check(false, BigDecimal.valueOf(5), 2, BigDecimal.valueOf(5), BigDecimal.valueOf(2), BigDecimal.class)),
            dynamicTest("x_bigdecimal_true", () -> check(true, 5, BigDecimal.valueOf(5), BigDecimal.valueOf(5), BigDecimal.valueOf(5), BigDecimal.class)),
            dynamicTest("x_bigdecimal_false", () -> check(false, 3, BigDecimal.valueOf(5), BigDecimal.valueOf(3), BigDecimal.valueOf(5), BigDecimal.class)),
            dynamicTest("float_x_true", () -> check(true, 1f, 1, 1.0, 1.0, Double.class)),
            dynamicTest("float_x_false", () -> check(false, 1f, 2, 1.0, 2.0, Double.class)),
            dynamicTest("x_float_true", () -> check(true, 1, 1f, 1.0, 1.0, Double.class)),
            dynamicTest("x_float_false", () -> check(false, 1, 2f, 1.0, 2.0, Double.class)),
            dynamicTest("double_x_true", () -> check(true, 3.0, 3, 3.0, 3.0, Double.class)),
            dynamicTest("double_x_false", () -> check(false, 3.0, 4, 3.0, 4.0, Double.class)),
            dynamicTest("x_double_true", () -> check(true, 3, 3.0, 3.0, 3.0, Double.class)),
            dynamicTest("x_double_false", () -> check(false, 3, 4.0, 3.0, 4.0, Double.class)),
            dynamicTest("biginteger_x_true", () -> check(true, BigInteger.valueOf(2), "2.0", BigInteger.valueOf(2), BigInteger.valueOf(2), BigInteger.class)),
            dynamicTest("biginteger_x_false", () -> check(false, BigInteger.valueOf(2), "3.0", BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.class)),
            dynamicTest("x_biginteger_true", () -> check(true, "2.0", BigInteger.valueOf(2), BigInteger.valueOf(2), BigInteger.valueOf(2), BigInteger.class)),
            dynamicTest("x_biginteger_false", () -> check(false, "2.0", BigInteger.valueOf(3), BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.class)),
            dynamicTest("long_x_true", () -> check(true, 3L, 3, 3L, 3L, Long.class)),
            dynamicTest("long_x_false", () -> check(false, 3L, 4, 3L, 4L, Long.class)),
            dynamicTest("x_long_true", () -> check(true, 3, 3L, 3L, 3L, Long.class)),
            dynamicTest("x_long_false", () -> check(false, 3, 4L, 3L, 4L, Long.class)),
            dynamicTest("int_x_true", () -> check(true, 1, true, 1L, 1L, Long.class)),
            dynamicTest("int_x_false", () -> check(false, 1, false, 1L, 0L, Long.class)),
            dynamicTest("x_int_true", () -> check(true, true, 1, 1L, 1L, Long.class)),
            dynamicTest("x_int_false", () -> check(false, false, 1, 0L, 1L, Long.class)),
            dynamicTest("short_x_true", () -> check(true, (short) 1, true, 1L, 1L, Long.class)),
            dynamicTest("short_x_false", () -> check(false, (short) 1, false, 1L, 0L, Long.class)),
            dynamicTest("x_short_true", () -> check(true, true, (short) 1, 1L, 1L, Long.class)),
            dynamicTest("x_short_false", () -> check(false, false, (short) 1, 0L, 1L, Long.class)),
            dynamicTest("byte_x_true", () -> check(true, (byte) 1, true, 1L, 1L, Long.class)),
            dynamicTest("byte_x_false", () -> check(false, (byte) 1, false, 1L, 0L, Long.class)),
            dynamicTest("x_byte_true", () -> check(true, true, (byte) 1, 1L, 1L, Long.class)),
            dynamicTest("x_byte_false", () -> check(false, false, (byte) 1, 0L, 1L, Long.class)),
            dynamicTest("char_x_true", () -> check(true, 'a', true, 1L, 1L, Long.class)),
            dynamicTest("char_x_false", () -> check(false, 'a', false, 1L, 0L, Long.class)),
            dynamicTest("x_char_true", () -> check(true, true, 'a', 1L, 1L, Long.class)),
            dynamicTest("x_char_false", () -> check(false, false, 'a', 0L, 1L, Long.class)),
            dynamicTest("bool_x_true", () -> check(true, true, new Object(), true, true, Boolean.class)),
            dynamicTest("bool_x_false", () -> check(false, true, new Object(), true, false, Boolean.class)),
            dynamicTest("x_bool_true", () -> check(true, new Object(), true, true, true, Boolean.class)),
            dynamicTest("x_bool_false", () -> check(false, new Object(), false, false, true, Boolean.class)),
            dynamicTest("enum_x_true", () -> check(true, Locale.Category.DISPLAY, new Object(), Locale.Category.DISPLAY, Locale.Category.DISPLAY, Locale.Category.class)),
            dynamicTest("enum_x_false", () -> check(false, Locale.Category.DISPLAY, new Object(), Locale.Category.DISPLAY, Locale.Category.FORMAT, Locale.Category.class)),
            dynamicTest("x_enum_true", () -> check(true, new Object(), Locale.Category.DISPLAY, Locale.Category.DISPLAY, Locale.Category.DISPLAY, Locale.Category.class)),
            dynamicTest("x_enum_false", () -> check(false, new Object(), Locale.Category.DISPLAY, Locale.Category.FORMAT, Locale.Category.DISPLAY, Locale.Category.class)),
            dynamicTest("string_x_true", () -> check(true, "a", new Object(), "a", "a", String.class)),
            dynamicTest("string_x_false", () -> check(false, "a", new Object(), "a", "b", String.class)),
            dynamicTest("x_string_true", () -> check(true, new Object(), "a", "a", "a", String.class)),
            dynamicTest("x_string_false", () -> check(false, new Object(), "a", "b", "a", String.class)),
            dynamicTest("other_true", () -> check(true, List.of(1), List.of(1), null, null, null)),
            dynamicTest("other_false", () -> check(false, List.of(1), List.of(2), null, null, null))
        );
    }

    @Test
    public void testCompare_EqualsError() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EqualValueExpression(
            new StaticValueExpression(new Object() {
                @Override
                public boolean equals(Object obj) {
                    throw new RuntimeException("Error in equals");
                }
            }),
            new StaticValueExpression(new Object()));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
        assertEquals("Failed to compare values", ex.getMessage());
        assertEquals("Error in equals", ex.getCause().getMessage());
    }

    public void check(
            boolean expectedResult,
            Object leftValue, Object rightValue,
            Object targetLeftValue, Object targetRightValue, Class<?> targetType) {
        ExpressionFactory ef;
        if (targetType == null) {
            ef = ExpressionFactoryStubs.NONE;
        } else {
            ef = ExpressionFactoryStubs.coerceToValue(
                new ExpressionFactoryStubs.ConversionRule(leftValue, targetLeftValue, targetType),
                new ExpressionFactoryStubs.ConversionRule(rightValue, targetRightValue, targetType));
        }
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EqualValueExpression(
            new StaticValueExpression(leftValue),
            new StaticValueExpression(rightValue));
        assertEquals(expectedResult, expr.getValue(context));
    }
}
