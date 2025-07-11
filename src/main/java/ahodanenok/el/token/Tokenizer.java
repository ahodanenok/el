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
    private Token pendingToken;

    public Tokenizer(Reader reader) {
        this.reader = new PushbackReader(reader, 2);
    }

    @Override
    public boolean hasNext() {
        if (pendingToken != null) {
            return true;
        }

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

    public Token peek() {
        if (pendingToken != null) {
            return pendingToken;
        }

        if (!hasNext()) {
            pendingToken = null;
            return null;
        }

        pendingToken = next();
        return pendingToken;
    }

    @Override
    public Token next() {
        Token token;
        if (pendingToken != null) {
            token = pendingToken;
            pendingToken = null;
            return token;
        }

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
                default -> {
                    if (isNumberDigit((char) ch)
                            || (ch == '.' && isNumberDigit((char) peekChar()))) {
                        reader.unread(ch);
                        yield readNumberToken();
                    } else if (ch == '.') {
                        yield createToken(DOT, ".");
                    } else if (ch == '"' || ch == '\'') {
                        reader.unread(ch);
                        yield readStringToken();
                    }

                    reader.unread(ch);
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

    private int peekChar() throws IOException {
        int ch = reader.read();
        if (ch == -1) {
            return -1;
        }

        reader.unread(ch);
        return ch;
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

    private boolean isNumberDigit(char ch) {
        return ch >= '\u0030' && ch <= '\u0039';
    }

    private boolean isIdentifierDigit(char ch) {
        return (ch >= '\u0030' && ch <= '\u0039')
            || (ch >= '\u0660' && ch <= '\u0669')
            || (ch >= '\u06f0' && ch <= '\u06f9')
            || (ch >= '\u0966' && ch <= '\u096f')
            || (ch >= '\u09e6' && ch <= '\u09ef')
            || (ch >= '\u0a66' && ch <= '\u0a6f')
            || (ch >= '\u0ae6' && ch <= '\u0aef')
            || (ch >= '\u0b66' && ch <= '\u0b6f')
            || (ch >= '\u0be7' && ch <= '\u0bef')
            || (ch >= '\u0c66' && ch <= '\u0c6f')
            || (ch >= '\u0ce6' && ch <= '\u0cef')
            || (ch >= '\u0d66' && ch <= '\u0d6f')
            || (ch >= '\u0e50' && ch <= '\u0e59')
            || (ch >= '\u0ed0' && ch <= '\u0ed9')
            || (ch >= '\u1040' && ch <= '\u1049');
    }

    private boolean isLetter(char ch) {
        return ch == '\u0024'
            || (ch >= '\u0041' && ch <= '\u005a')
            || ch == '\u005f'
            || (ch >= '\u0061' && ch <= '\u007a')
            || (ch >= '\u00c0' && ch <= '\u00d6')
            || (ch >= '\u00d8' && ch <= '\u00f6')
            || (ch >= '\u00f8' && ch <= '\u00ff')
            || (ch >= '\u0100' && ch <= '\u1fff')
            || (ch >= '\u3040' && ch <= '\u318f')
            || (ch >= '\u3300' && ch <= '\u337f')
            || (ch >= '\u3400' && ch <= '\u3d2d')
            || (ch >= '\u4e00' && ch <= '\u9fff')
            || (ch >= '\uf900' && ch <= '\ufaff');
    }

    private Token readNumberToken() throws IOException {
        // todo: negative numbers

        boolean dot = false;
        boolean exponent = false;
        StringBuilder buf = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            if (isNumberDigit((char) ch)) {
                buf.append((char) ch);
            } else if (!dot && ch == '.') {
                dot = true;
                buf.append((char) ch);
            } else if (!exponent && (ch == 'e' || ch == 'E')) {
                exponent = true;
                buf.append((char) ch);
                if (match('+')) {
                    buf.append('+');
                } else if (match('-')) {
                    buf.append('-');
                }
            } else {
                reader.unread(ch);
                break;
            }
        }

        if (buf.isEmpty()) {
            throw new IllegalStateException("Expected a number");
        }

        String lexeme = buf.toString();
        if (dot || exponent) {
            try {
                return createToken(FLOAT, lexeme, Double.parseDouble(lexeme));
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid float literal '%s'".formatted(lexeme));
            }
        } else {
            try {
                return createToken(INTEGER, lexeme, Long.parseLong(lexeme));
            } catch (NumberFormatException e) {
                throw new IllegalStateException(
                    "Integer literal '%s' is too large".formatted(lexeme));
            }
        }
    }

    private Token readStringToken() throws IOException {
        StringBuilder buf = new StringBuilder();

        char quote;
        if (match('"')) {
            quote = '"';
        } else if (match('\'')) {
            quote = '\'';
        } else {
            throw new IllegalStateException("Expected a string");
        }
        buf.append(quote);

        int ch, nextCh;
        while ((ch = reader.read()) != -1 && ch != quote) {
            if (ch == '\\') {
                nextCh = peekChar();
                if (nextCh == '\\') {
                    buf.append((char) reader.read());
                } else if (nextCh == quote) {
                    buf.append((char) reader.read());
                } else {
                    throw new IllegalStateException(
                        "Unsupported escape sequence '%c%c'".formatted(ch, nextCh));
                }
            } else {
                buf.append((char) ch);
            }
        }

        if (ch == quote) {
            buf.append(quote);
        } else {
            throw new IllegalStateException("Unclosed string literal");
        }

        return createToken(STRING, buf.toString(), buf.substring(1, buf.length() - 1));
    }

    private String readIdentifier() throws IOException {
        StringBuilder buf = new StringBuilder();
        int ch = reader.read();
        if (ch == -1) {
            throw new IllegalStateException("Expected identifier");
        }

        if (!isLetter((char) ch)) {
            throw new IllegalStateException("First character of an identifier must be a letter, got '%c'".formatted(ch));
        }
        buf.append((char) ch);

        while ((ch = reader.read()) != -1
                && (isLetter((char) ch) || isIdentifierDigit((char) ch))) {
            buf.append((char) ch);
        }
        if (ch != -1) {
            reader.unread(ch);
        }

        return buf.toString();
    }
}
