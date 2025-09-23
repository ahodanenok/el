package ahodanenok.el.expression;

import static ahodanenok.el.token.TokenType.COLON;

import java.util.ArrayList;
import java.util.List;

import ahodanenok.el.token.LookaheadTokenizer;
import ahodanenok.el.token.Token;
import ahodanenok.el.token.TokenType;
import ahodanenok.el.token.Tokenizer;
import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

public class Parser {

    private final LookaheadTokenizer tokenizer;
    private final ELContext context;

    public Parser(Tokenizer tokenizer, ELContext context) {
        this.tokenizer = new LookaheadTokenizer(tokenizer, 5);
        this.context = context;
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

        Token token = tokenizer.peek(1);
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
        ValueExpressionBase expr = assignment();
        if (match(TokenType.SEMICOLON)) {
            List<ValueExpressionBase> expressions = new ArrayList<>();
            expressions.add(expr);
            do {
                expressions.add(assignment());
            } while (match(TokenType.SEMICOLON));
            expr = new SemicolonValueExpression(expressions);
        }

        return expr;
    }

    private ValueExpressionBase assignment() {
        ValueExpressionBase expr = lambda();
        if (match(TokenType.EQUAL)) {
            // todo: validate left side is an lvalue expression
            expr = new AssignValueExpression(expr, assignment());
        }

        return expr;
    }

    private ValueExpressionBase lambda() {
        if (peek(TokenType.IDENTIFIER, TokenType.ARROW)) {
            Token param = tokenizer.next();
            expect(TokenType.ARROW);

            return new LambdaValueExpression(List.of(param.getLexeme()), expression());
        } else if (peek(TokenType.PAREN_LEFT, TokenType.PAREN_RIGHT, TokenType.ARROW)
                || peek(TokenType.PAREN_LEFT, TokenType.IDENTIFIER, TokenType.COMMA)
                || peek(TokenType.PAREN_LEFT, TokenType.IDENTIFIER, TokenType.PAREN_RIGHT, TokenType.ARROW)) {
            List<String> params = new ArrayList<>();
            expect(TokenType.PAREN_LEFT);
            while (peek(TokenType.IDENTIFIER)) {
                params.add(tokenizer.next().getLexeme());
                if (!peek(TokenType.PAREN_RIGHT)) {
                    expect(TokenType.COMMA);
                }
            }
            expect(TokenType.PAREN_RIGHT);
            expect(TokenType.ARROW);

            return new LambdaValueExpression(params, expression());
        } else {
            return conditional();
        }
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
            return property();
        }
    }

    private ValueExpressionBase property() {
        ValueExpressionBase expr = base();
        while (true) {
            // todo: make better
            String propertyName;
            ValueExpressionBase propertyExpr;
            boolean dot = false;
            if (match(TokenType.SQUARE_LEFT)) {
                propertyName = null;
                propertyExpr = expression();
                expect(TokenType.SQUARE_RIGHT);
            } else if (match(TokenType.DOT)) {
                Token id = expect(TokenType.IDENTIFIER);
                propertyName = id.getLexeme();
                propertyExpr = new StaticValueExpression(id.getLexeme());
                dot = true;
            } else {
                break;
            }

            if (match(TokenType.PAREN_LEFT)) {
                List<ValueExpression> params = new ArrayList<>();
                if (!match(TokenType.PAREN_RIGHT)) {
                    do {
                        params.add(expression());
                    } while (match(TokenType.COMMA));
                    expect(TokenType.PAREN_RIGHT);
                }
                expr = new PropertyCallValueExpression(expr, propertyExpr, params, dot);
            } else {
                expr = new PropertyAccessValueExpression(expr, propertyName, propertyExpr);
            }
        }

        return expr;
    }

    private ValueExpressionBase base() {
        Token token = tokenizer.next();
        return switch (token.getType()) {
            case BOOLEAN -> new StaticValueExpression(token.getValue());
            case STRING -> new StaticValueExpression(token.getValue());
            case INTEGER -> new StaticValueExpression(token.getValue());
            case FLOAT -> new StaticValueExpression(token.getValue());
            case NULL -> new StaticValueExpression(token.getValue());
            case IDENTIFIER -> {
                if (match(COLON)) {
                    Token localName = expect(TokenType.IDENTIFIER);
                    expect(TokenType.PAREN_LEFT);
                    yield functionCall(token.getLexeme(), localName.getLexeme());
                } else if (match(TokenType.PAREN_LEFT)) {
                    yield functionCall(null, token.getLexeme());
                } else {
                    ValueExpression mappedExpr;
                    if (context.getVariableMapper() != null) {
                        mappedExpr = context.getVariableMapper().resolveVariable(token.getLexeme());
                    } else {
                        mappedExpr = null;
                    }

                    yield new IdentifierValueExpression(token.getLexeme(), mappedExpr);
                }
            }
            default -> throw new IllegalStateException("Unexpected token: " + token.getType()); // todo: exception
        };
    }

    private ValueExpressionBase functionCall(String prefix, String localName) {
        ValueExpression variableExpr;
        if (prefix == null && context.getVariableMapper() != null) {
            variableExpr = context.getVariableMapper().resolveVariable(localName);
        } else {
            variableExpr = null;
        }

        FunctionCallValueExpression call = new FunctionCallValueExpression(
            prefix, localName,
            variableExpr,
            context.getFunctionMapper().resolveFunction(prefix, localName),
            args());
        expect(TokenType.PAREN_RIGHT);

        while (match(TokenType.PAREN_LEFT)) {
            call = new FunctionCallValueExpression(call, args());
            expect(TokenType.PAREN_RIGHT);
        }

        return call;
    }

    private List<ValueExpressionBase> args() {
        List<ValueExpressionBase> args = new ArrayList<>();
        if (peek(TokenType.PAREN_RIGHT)) {
            return args;
        }

        args.add(expression());
        while (!peek(TokenType.PAREN_RIGHT)) {
            expect(TokenType.COMMA);
            args.add(expression());
        }

        return args;
    }

    private ValueExpressionBase literal() {
        Token token = tokenizer.next();
        if (token.getType() == TokenType.STRING) {
            return new StaticValueExpression(token.getValue());
        } else {
            return new StaticValueExpression(token.getLexeme());
        }
    }

    private Token expect(TokenType tokenType) {
        if (!tokenizer.hasNext()) {
            throw new IllegalStateException("Expected " + tokenType);
        }

        Token token = tokenizer.peek(1);
        if (token.getType() != tokenType) {
            throw new IllegalStateException("Expected %s, got %s".formatted(tokenType, token.getType()));
        }

        return tokenizer.next();
    }

    private boolean match(TokenType tokenType) {
        if (peek(tokenType)) {
            tokenizer.next();
            return true;
        } else {
            return false;
        }
    }

    private boolean peek(TokenType... tokenTypes) {
        for (int i = 0; i < tokenTypes.length; i++) {
            Token token = tokenizer.peek(i + 1);
            if (token == null) {
                return false;
            }

            if (token.getType() != tokenTypes[i]) {
                return false;
            }
        }

        return true;
    }
}
