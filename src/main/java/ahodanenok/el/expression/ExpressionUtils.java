package ahodanenok.el.expression;

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
}
