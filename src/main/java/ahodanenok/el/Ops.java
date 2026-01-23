package ahodanenok.el;

import jakarta.el.ELContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

// todo: move all operations here?
public final class Ops {

    private Ops() { }

    public static Number add(ELContext context, Object a, Object b) {
        if (a == null && b == null) {
            return Long.valueOf(0);
        } else if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return context.convertToType(a, BigDecimal.class)
                .add(context.convertToType(b, BigDecimal.class));
        } else if (a instanceof Float
                || a instanceof Double
                || (a instanceof String s && looksLikeDouble(s))
                || b instanceof Float
                || b instanceof Double
                || (b instanceof String s && looksLikeDouble(s))) {
            if (a instanceof BigInteger || b instanceof BigInteger) {
                return context.convertToType(a, BigDecimal.class)
                    .add(context.convertToType(b, BigDecimal.class));
            } else {
                return Double.valueOf(
                    context.convertToType(a, Double.class)
                    + context.convertToType(b, Double.class));
            }
        } else if (a instanceof BigInteger || b instanceof BigInteger) {
            return context.convertToType(a, BigInteger.class)
                .add(context.convertToType(b, BigInteger.class));
        } else {
            return Long.valueOf(
                context.convertToType(a, Long.class)
                + context.convertToType(b, Long.class));
        }
    }

    public static Number divide(ELContext context, Object a, Object b) {
        if (a == null && b == null) {
            return Long.valueOf(0L);
        } else if (a instanceof BigDecimal
                || a instanceof BigInteger
                || b instanceof BigDecimal
                || b instanceof BigInteger) {
            return context.convertToType(a, BigDecimal.class)
                .divide(context.convertToType(b, BigDecimal.class), RoundingMode.HALF_UP);
        } else {
            return Double.valueOf(
                context.convertToType(a, Double.class)
                / context.convertToType(b, Double.class));
        }
    }

    // todo: shouldn't be visible outside
    public static boolean looksLikeDouble(String str) {
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch == '.' || ch == 'e' || ch == 'E') {
                return true;
            }
        }

        return false;
    }
}