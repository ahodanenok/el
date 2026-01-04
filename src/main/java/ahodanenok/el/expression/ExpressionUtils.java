package ahodanenok.el.expression;

import java.util.List;

import jakarta.el.ELContext;

class ExpressionUtils {

    static boolean looksLikeDouble(String str) {
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch == '.' || ch == 'e' || ch == 'E') {
                return true;
            }
        }

        return false;
    }

    static Object[] evaluateArgs(ELContext context, List<ValueExpressionBase> args) {
        Object[] values = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            values[i] = args.get(i).getValue(context);
        }

        return values;
    }

    static Class<?>[] collectArgTypes(Object[] args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                argTypes[i] = args[i].getClass();
            } else {
                argTypes[i] = null;
            }
        }

        return argTypes;
    }
}
