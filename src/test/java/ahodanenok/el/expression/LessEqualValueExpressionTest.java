package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class LessEqualValueExpressionTest {

    @TestFactory
    public List<DynamicTest> testCompare() {
        return List.of(
            dynamicTest("same", () -> check(true, "a", "a", "a", "a", String.class)),
            dynamicTest("nulls", () -> check(true, null, null, null, null, null)),
            dynamicTest("null_x", () -> check(false, null, 1, null, null, null)),
            dynamicTest("x_null", () -> check(false, 1, null, null, null, null)),
            dynamicTest("bigdecimal_x_lt_true", () -> check(true, BigDecimal.valueOf(1.2), 2, BigDecimal.valueOf(1.2), BigDecimal.valueOf(2), BigDecimal.class)),
            dynamicTest("bigdecimal_x_eq_true", () -> check(true, BigDecimal.valueOf(1.2), 1.2, BigDecimal.valueOf(1.2), BigDecimal.valueOf(1.2), BigDecimal.class)),
            dynamicTest("bigdecimal_x_false", () -> check(false, BigDecimal.valueOf(1.2), 1.1, BigDecimal.valueOf(1.2), BigDecimal.valueOf(1.1), BigDecimal.class)),
            dynamicTest("x_bigdecimal_lt_true", () -> check(true, 10, BigDecimal.valueOf(11), BigDecimal.valueOf(10), BigDecimal.valueOf(11), BigDecimal.class)),
            dynamicTest("x_bigdecimal_eq_true", () -> check(true, 10, BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.class)),
            dynamicTest("x_bigdecimal_false", () -> check(false, 20, BigDecimal.valueOf(19), BigDecimal.valueOf(20), BigDecimal.valueOf(19), BigDecimal.class)),
            dynamicTest("float_x_lt_true", () -> check(true, 2.5f, 3, 2.5, 3.0, Double.class)),
            dynamicTest("float_x_eq_true", () -> check(true, 2f, 2, 2.0, 2.0, Double.class)),
            dynamicTest("float_x_false", () -> check(false, 2.5f, 2, 2.5, 2.0, Double.class)),
            dynamicTest("x_float_lt_true", () -> check(true, 2, 2.5f, 2.0, 2.5, Double.class)),
            dynamicTest("x_float_eq_true", () -> check(true, 2, 2f, 2.0, 2.0, Double.class)),
            dynamicTest("x_float_false", () -> check(false, 2, 1.5f, 2.0, 1.5, Double.class)),
            dynamicTest("double_x_lt_true", () -> check(true, 2.5, 3, 2.5, 3.0, Double.class)),
            dynamicTest("double_x_eq_true", () -> check(true, 3.0, 3, 3.0, 3.0, Double.class)),
            dynamicTest("double_x_false", () -> check(false, 2.5, 2, 2.5, 2.0, Double.class)),
            dynamicTest("x_double_lt_true", () -> check(true, 2, 2.5, 2.0, 2.5, Double.class)),
            dynamicTest("x_double_eq_true", () -> check(true, 2, 2.0, 2.0, 2.0, Double.class)),
            dynamicTest("x_double_false", () -> check(false, 2, 1.5, 2.0, 1.5, Double.class)),
            dynamicTest("biginteger_x_lt_true", () -> check(true, BigInteger.valueOf(2), 3, BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.class)),
            dynamicTest("biginteger_x_eq_true", () -> check(true, BigInteger.valueOf(2), 2, BigInteger.valueOf(2), BigInteger.valueOf(2), BigInteger.class)),
            dynamicTest("biginteger_x_false", () -> check(false, BigInteger.valueOf(2), 1, BigInteger.valueOf(2), BigInteger.valueOf(1), BigInteger.class)),
            dynamicTest("byte_x_lt_true", () -> check(true, (byte) 2, 3, 2L, 3L, Long.class)),
            dynamicTest("byte_x_eq_true", () -> check(true, (byte) 2, 2, 2L, 2L, Long.class)),
            dynamicTest("byte_x_false", () -> check(false, (byte) 3, 2, 3L, 2L, Long.class)),
            dynamicTest("x_byte_lt_true", () -> check(true, 2, (byte) 3, 2L, 3L, Long.class)),
            dynamicTest("x_byte_eq_true", () -> check(true, 2, (byte) 2, 2L, 2L, Long.class)),
            dynamicTest("x_byte_false", () -> check(false, 3, (byte) 2, 3L, 2L, Long.class)),
            dynamicTest("short_x_lt_true", () -> check(true, (short) 2, 3, 2L, 3L, Long.class)),
            dynamicTest("short_x_eq_true", () -> check(true, (short) 2, 2, 2L, 2L, Long.class)),
            dynamicTest("short_x_false", () -> check(false, (short) 3, 2, 3L, 2L, Long.class)),
            dynamicTest("x_short_lt_true", () -> check(true, 2, (short) 3, 2L, 3L, Long.class)),
            dynamicTest("x_short_eq_true", () -> check(true, 2, (short) 2, 2L, 2L, Long.class)),
            dynamicTest("x_short_false", () -> check(false, 3, (short) 2, 3L, 2L, Long.class)),
            dynamicTest("integer_x_lt_true", () -> check(true, 2, "3", 2L, 3L, Long.class)),
            dynamicTest("integer_x_eq_true", () -> check(true, 3, "3", 3L, 3L, Long.class)),
            dynamicTest("integer_x_false", () -> check(false, 3, "2", 3L, 2L, Long.class)),
            dynamicTest("x_integer_lt_true", () -> check(true, "2", 3, 2L, 3L, Long.class)),
            dynamicTest("x_integer_eq_true", () -> check(true, "3", 3, 3L, 3L, Long.class)),
            dynamicTest("x_integer_false", () -> check(false, "3", 2, 3L, 2L, Long.class)),
            dynamicTest("long_x_lt_true", () -> check(true, 2L, "3", 2L, 3L, Long.class)),
            dynamicTest("long_x_eq_true", () -> check(true, 2L, "2", 2L, 2L, Long.class)),
            dynamicTest("long_x_false", () -> check(false, 3L, "2", 3L, 2L, Long.class)),
            dynamicTest("x_long_lt_true", () -> check(true, "2", 3L, 2L, 3L, Long.class)),
            dynamicTest("x_long_eq_true", () -> check(true, "2", 2L, 2L, 2L, Long.class)),
            dynamicTest("x_long_false", () -> check(false, "3", 2L, 3L, 2L, Long.class)),
            dynamicTest("char_x_lt_true", () -> check(true, 'a', 61, 60L, 61L, Long.class)),
            dynamicTest("char_x_eq_true", () -> check(true, 'a', 60, 60L, 60L, Long.class)),
            dynamicTest("char_x_false", () -> check(false, 'a', 59, 60L, 59L, Long.class)),
            dynamicTest("x_char_lt_true", () -> check(true, 59, 'a', 59L, 60L, Long.class)),
            dynamicTest("x_char_eq_true", () -> check(true, 60, 'a', 60L, 60L, Long.class)),
            dynamicTest("x_char_false", () -> check(false, 61, 'a', 61L, 60L, Long.class)),
            dynamicTest("x_biginteger_lt_true", () -> check(true, "1", BigInteger.valueOf(2), BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.class)),
            dynamicTest("x_biginteger_eq_true", () -> check(true, "2", BigInteger.valueOf(2), BigInteger.valueOf(2), BigInteger.valueOf(2), BigInteger.class)),
            dynamicTest("x_biginteger_false", () -> check(false, "2", BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(1), BigInteger.class)),
            dynamicTest("string_x_lt_true", () -> check(true, "a", new Object(), "a", "b", String.class)),
            dynamicTest("string_x_eq_true", () -> check(true, "a", new Object(), "a", "a", String.class)),
            dynamicTest("string_x_false", () -> check(false, "b", new Object(), "b", "a", String.class)),
            dynamicTest("x_string_lt_true", () -> check(true, new Object(), "b", "a", "b", String.class)),
            dynamicTest("x_string_eq_true", () -> check(true, new Object(), "b", "b", "b", String.class)),
            dynamicTest("x_string_false", () -> check(false, new Object(), "a", "b", "a", String.class)),
            dynamicTest("comparable_lt_true", () -> check(true, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2), null, null, null)),
            dynamicTest("comparable_eq_true", () -> check(true, LocalDate.of(2025, 1, 2), LocalDate.of(2025, 1, 2), null, null, null)),
            dynamicTest("comparable_false", () -> check(false, LocalDate.of(2025, 1, 2), LocalDate.of(2025, 1, 1), null, null, null))
        );
    }

    @Test
    public void testCompare_Error() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new LessEqualValueExpression(
            new StaticValueExpression(new ArrayList<>()),
            new StaticValueExpression(new Object()));
        ELException ex = assertThrows(ELException.class, () -> expr.getValue(context));
        assertEquals("Comparison of types 'java.util.ArrayList' and 'java.lang.Object' is not supported", ex.getMessage());
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
        var expr = new LessEqualValueExpression(
            new StaticValueExpression(leftValue),
            new StaticValueExpression(rightValue));
        assertEquals(expectedResult, expr.getValue(context));
    }
}
