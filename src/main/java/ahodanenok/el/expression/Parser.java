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
            case DOLLAR -> dollarExpression();
            case HASH -> hashExpression();
            default -> literal();
        };
    }

    private ValueExpressionBase dollarExpression() {
        expect(TokenType.DOLLAR);
        expect(TokenType.CURLY_LEFT);
        ValueExpressionBase expr = expression();
        expect(TokenType.CURLY_RIGHT);
        return expr;
    }

    private ValueExpressionBase hashExpression() {
        expect(TokenType.HASH);
        expect(TokenType.CURLY_LEFT);
        ValueExpressionBase expr = expression();
        expect(TokenType.CURLY_RIGHT);
        return expr;
    }

    private ValueExpressionBase expression() {
        return semicolon();
    }

    private ValueExpressionBase semicolon() {
        ValueExpressionBase expr = conditional();
        if (match(TokenType.SEMICOLON)) {
            List<ValueExpressionBase> expressions = new ArrayList<>();
            expressions.add(expr);
            do {
                expressions.add(conditional());
            } while (match(TokenType.SEMICOLON));
            expr = new SemicolonValueExpression(expressions);
        }

        return expr;
    }

    private ValueExpressionBase conditional() {
        ValueExpressionBase expr = or();
        if (match(TokenType.QUESTION)) {
            ValueExpressionBase onTrue = conditional();
            expect(TokenType.COLON);
            ValueExpressionBase onFalse = conditional();
            expr = new ConditionalValueExpression(expr, onTrue, onFalse);
        }

        return expr;
    }

    private ValueExpressionBase or() {
        ValueExpressionBase expr = and();
        while (match(TokenType.BAR_BAR) || match(TokenType.OR)) {
            expr = new OrValueExpression(expr, and());
        }

        return expr;
    }

    private ValueExpressionBase and() {
        ValueExpressionBase expr = equal();
        while (match(TokenType.AMP_AMP) || match(TokenType.AND)) {
            expr = new AndValueExpression(expr, equal());
        }

        return expr;
    }

    private ValueExpressionBase equal() {
        ValueExpressionBase expr = compare();
        while (true) {
            if (match(TokenType.EQUAL_EQUAL) || match(TokenType.EQ)) {
                expr = new EqualValueExpression(expr, compare());
            } else if (match(TokenType.BANG_EQUAL) || match(TokenType.NE)) {
                expr = new NotEqualValueExpression(expr, compare());
            } else {
                break;
            }
        }

        return expr;
    }

    private ValueExpressionBase compare() {
        ValueExpressionBase expr = concatenate();
        while (true) {
            if (match(TokenType.ANGLE_LEFT) || match(TokenType.LT)) {
                expr = new LessThanValueExpression(expr, concatenate());
            } else if (match(TokenType.ANGLE_LEFT_EQUAL) || match(TokenType.LE)) {
                expr = new LessEqualValueExpression(expr, concatenate());
            } else if (match(TokenType.ANGLE_RIGHT) || match(TokenType.GT)) {
                expr = new GreaterThanValueExpression(expr, concatenate());
            } else if (match(TokenType.ANGLE_RIGHT_EQUAL) || match(TokenType.GE)) {
                expr = new GreaterEqualValueExpression(expr, concatenate());
            } else {
                break;
            }
        }

        return expr;
    }

    private ValueExpressionBase concatenate() {
        ValueExpressionBase expr = add();
        while (match(TokenType.PLUS_EQUAL)) {
            expr = new ConcatenateValueExpression(expr, add());
        }

        return expr;
    }

    private ValueExpressionBase add() {
        ValueExpressionBase expr = multiply();
        while (true) {
            if (match(TokenType.PLUS)) {
                expr = new AddValueExpression(expr, multiply());
            } else if (match(TokenType.MINUS)) {
                expr = new SubtractValueExpression(expr, multiply());
            } else {
                break;
            }
        }

        return expr;
    }

    private ValueExpressionBase multiply() {
        ValueExpressionBase expr = unary();
        while (true) {
            if (match(TokenType.STAR)) {
                expr = new MultiplyValueExpression(expr, unary());
            } else if (match(TokenType.SLASH) || match(TokenType.DIV)) {
                expr = new DivideValueExpression(expr, unary());
            } else if (match(TokenType.PERCENT) || match(TokenType.MOD)) {
                expr = new ModuloValueExpression(expr, unary());
            } else {
                break;
            }
        }

        return expr;
    }

    private ValueExpressionBase unary() {
        if (match(TokenType.BANG) || match(TokenType.NOT)) {
            return new NotValueExpression(expression());
        } else if (match(TokenType.MINUS)) {
            return new NegateValueExpression(expression());
        } else if (match(TokenType.EMPTY)) {
            return new EmptyValueExpression(expression());
        } else {
            return parenthesis();
        }
    }

    private ValueExpressionBase parenthesis() {
        if (match(TokenType.PAREN_LEFT)) {
            ParenthesisValueExpression expr = new ParenthesisValueExpression(expression());
            expect(TokenType.PAREN_RIGHT);
            return expr;
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
