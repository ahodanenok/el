package ahodanenok.el.token;

public class Token {

    private final TokenType type;
    private final String lexeme;
    private final Object value;
    // todo: line? col?

    public Token(TokenType type, String lexeme) {
        this(type, lexeme, null);
    }

    public Token(TokenType type, String lexeme, Object value) {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }
}
