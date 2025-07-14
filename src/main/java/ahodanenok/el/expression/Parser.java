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

    public ValueExpressionBase parseValue() {
        return composite();
    }

    private ValueExpressionBase composite() {
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

    private ValueExpressionBase dollarValue() {
        expect(TokenType.DOLLAR);
        expect(TokenType.CURLY_LEFT);
        ValueExpressionBase expr = expression();
        expect(TokenType.CURLY_RIGHT);
        return expr;
    }

    private ValueExpressionBase hashValue() {
        expect(TokenType.HASH);
        expect(TokenType.CURLY_LEFT);
        ValueExpressionBase expr = expression();
        expect(TokenType.CURLY_RIGHT);
        return expr;
    }

    private ValueExpressionBase expression() {
        return unary();
    }

    private ValueExpressionBase unary() {
        if (match(TokenType.BANG) || match(TokenType.NOT)) {
            return new NotValueExpression(expression());
        } else {
            return literal();
        }
    }

    private ValueExpressionBase literal() {
        Token token = tokenizer.next();
        return switch (token.getType()) {
            case BOOLEAN -> new StaticValueExpression(token.getValue());
            case STRING -> new StaticValueExpression(token.getValue());
            case INTEGER -> new StaticValueExpression(token.getValue());
            case FLOAT -> new StaticValueExpression(token.getValue());
            case NULL -> new StaticValueExpression(token.getValue());
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
