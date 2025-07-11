package ahodanenok.el.expression;

import java.util.ArrayList;
import java.util.List;

import ahodanenok.el.token.Token;
import ahodanenok.el.token.TokenType;
import ahodanenok.el.token.Tokenizer;

public class Parser {

    private final Tokenizer tokenizer;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public ValueExpressionBase composite() {
        if (!tokenizer.hasNext()) {
            return null;
        }

        // todo: collect all
        // List<ValueExpressionBase> expressions = new ArrayList<>();

        Token token = tokenizer.peek();
        return switch (token.getType()) {
            case DOLLAR -> dollarValue();
            case HASH -> hashValue();
            default -> literal();
        };
    }

    public ValueExpressionBase dollarValue() {
        expect(TokenType.DOLLAR);
        expect(TokenType.CURLY_LEFT);
        ValueExpressionBase expr = literal();
        expect(TokenType.CURLY_RIGHT);
        return expr;
    }

    public ValueExpressionBase hashValue() {
        expect(TokenType.HASH);
        expect(TokenType.CURLY_LEFT);
        ValueExpressionBase expr = literal();
        expect(TokenType.CURLY_RIGHT);
        return expr;
    }

    public ValueExpressionBase literal() {
        Token token = tokenizer.next();
        return switch (token.getType()) {
            case BOOLEAN -> new StaticValueExpressionImpl(token.getValue());
            case STRING -> new StaticValueExpressionImpl(token.getValue());
            case INTEGER -> new StaticValueExpressionImpl(token.getValue());
            case FLOAT -> new StaticValueExpressionImpl(token.getValue());
            case NULL -> new StaticValueExpressionImpl(token.getValue());
            default -> throw new IllegalStateException("Unexpected token: " + token.getType()); // todo: exception
        };
    }

    private void expect(TokenType tokenType) {
        if (!match(tokenType)) {
            throw new IllegalStateException("Expected " + tokenType);
        }
    }

    private boolean match(TokenType tokenType) {
        if (!tokenizer.hasNext()) {
            return false;
        }

        Token token = tokenizer.peek();
        if (token.getType() != tokenType) {
            return false;
        }

        tokenizer.next();
        return true;
    }
}
