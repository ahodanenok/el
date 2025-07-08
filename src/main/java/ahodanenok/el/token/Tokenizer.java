package ahodanenok.el.token;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static ahodanenok.el.token.TokenType.*;

public class Tokenizer implements Iterator<Token> {

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("true", BOOLEAN);
        KEYWORDS.put("false", BOOLEAN);
        KEYWORDS.put("and", AND);
        KEYWORDS.put("or", OR);
        KEYWORDS.put("div", DIV);
        KEYWORDS.put("mod", MOD);
        KEYWORDS.put("eq", EQ);
        KEYWORDS.put("empty", EMPTY);
        KEYWORDS.put("ge", GE);
        KEYWORDS.put("gt", GT);
        KEYWORDS.put("ne", NE);
        KEYWORDS.put("le", LE);
        KEYWORDS.put("lt", LT);
        KEYWORDS.put("not", NOT);
        KEYWORDS.put("null", NULL);
    }

    private final PushbackReader reader;

    public Tokenizer(Reader reader) {
        this.reader = new PushbackReader(reader, 1);
    }

    @Override
    public boolean hasNext() {
        try {
            skipWhitespaces();

            int ch = reader.read();
            if (ch == -1) {
                return false;
            }

            reader.unread(ch);
            return true;
        } catch (IOException e) {
            // todo: throw exception?
            return false;
        }
    }

    @Override
    public Token next() {
        Token token;
        try {
            token = readNext();
        } catch (IOException e) {
            throw new IllegalStateException(e); // todo: what exception to throw?
        }

        if (token == null) {
            throw new NoSuchElementException("No more tokens");
        }

        return token;
    }

    private Token readNext() throws IOException {
        skipWhitespaces();
        while (true) {
            int ch = reader.read();
            if (ch == -1) {
                break;
            }

            return switch (ch) {
                case '+' -> {
                    if (match('=')) {
                        yield createToken(PLUS_EQUAL, "+=");
                    } else {
                        yield createToken(PLUS, "+");
                    }
                }
                case '-' -> {
                    if (match('>')) {
                        yield createToken(ARROW, "->");
                    } else {
                        yield createToken(MINUS, "-");
                    }
                }
                case '*' -> createToken(STAR, "*");
                case '/' -> createToken(SLASH, "/");
                case '%' -> createToken(PERCENT, "%");
                case '=' -> {
                    if (match('=')) {
                        yield createToken(EQUAL_EQUAL, "==");
                    } else {
                        yield createToken(EQUAL, "=");
                    }
                }
                case '?' -> createToken(QUESTION, "?");
                case ':' -> createToken(COLON, ":");
                case ';' -> createToken(SEMICOLON, ";");
                case '(' -> createToken(PAREN_LEFT, "(");
                case ')' -> createToken(PAREN_RIGHT, ")");
                case '!'-> {
                    if (match('=')) {
                        yield createToken(BANG_EQUAL, "!=");
                    } else {
                        yield createToken(BANG, "!");
                    }
                }
                case '<' -> {
                    if (match('=')) {
                        yield createToken(ANGLE_LEFT_EQUAL, "<=");
                    } else {
                        yield createToken(ANGLE_LEFT, "<");
                    }
                }
                case '&' -> {
                    if (match('&')) {
                        yield createToken(AMP_AMP, "&&");
                    } else {
                        throw new IllegalStateException("Unexpected character: '%s'".formatted(ch));
                    }
                }
                case '|' -> {
                    if (match('|')) {
                        yield createToken(BAR_BAR, "||");
                    } else {
                        throw new IllegalStateException("Unexpected character: '%s'".formatted(ch));
                    }
                }
                case '>' -> {
                    if (match('=')) {
                        yield createToken(ANGLE_RIGHT_EQUAL, ">=");
                    } else {
                        yield createToken(ANGLE_RIGHT, ">");
                    }
                }
                case '$' -> createToken(DOLLAR, "$");
                case '#' -> createToken(HASH, "#");
                case '{' -> createToken(CURLY_LEFT, "{");
                case '}' -> createToken(CURLY_RIGHT, "}");
                case '[' -> createToken(SQUARE_LEFT, "[");
                case ']' -> createToken(SQUARE_RIGHT, "]");
                case '.' -> createToken(DOT, ".");
                default -> {
                    if (isDigit((char) ch)) {
                        yield readNumberToken();
                    } else if (ch == '"') {
                        yield readStringToken();
                    }

                    String identifier = readIdentifier();
                    if (identifier.equals("true")) {
                        yield createToken(BOOLEAN, identifier, true);
                    } else if (identifier.equals("false")) {
                        yield createToken(BOOLEAN, identifier, false);
                    } else if (KEYWORDS.containsKey(identifier)) {
                        yield createToken(KEYWORDS.get(identifier), identifier);
                    } else {
                        yield createToken(IDENTIFIER, identifier);
                    }
                }
            };
        }

        return null;
    }

    private Token createToken(TokenType type, String lexeme) {
        return new Token(type, lexeme);
    }

    private Token createToken(TokenType type, String lexeme, Object value) {
        return new Token(type, lexeme, value);
    }

    private void skipWhitespaces() throws IOException {
        while (true) {
            int ch = reader.read();
            if (ch == -1) {
                break;
            }

            if (ch == ' ' || ch == '\t'
                    || ch == '\r' || ch == '\n') {
                continue;
            }

            reader.unread(ch);
            break;
        }
    }

    private boolean match(char expectedChar) throws IOException {
        int ch = reader.read();
        if (ch == -1) {
            return false;
        }

        if (ch == expectedChar) {
            return true;
        }

        reader.unread(ch);
        return false;
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private Token readNumberToken() {
        boolean dot = false;
        StringBuilder buf = new StringBuilder();
        // todo: impl

        String lexeme = buf.toString();
        if (dot) {
            return createToken(FLOAT, lexeme, Double.parseDouble(lexeme));
        } else {
            return createToken(INTEGER, lexeme, Integer.parseInt(lexeme));
        }
    }

    private Token readStringToken() {
        StringBuilder buf = new StringBuilder();
        // todo: impl

        return createToken(STRING, buf.toString(), buf.substring(1, buf.length() - 1));
    }

    private String readIdentifier() {
        StringBuilder buf = new StringBuilder();
        // todo: impl

        return buf.toString();
    }
}
