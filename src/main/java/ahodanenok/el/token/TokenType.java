package ahodanenok.el.token;

public enum TokenType {

    PLUS, // +
    PLUS_EQUAL, // +=
    MINUS, // -
    STAR, // *
    SLASH, // /
    DIV, // div
    PERCENT, // %
    MOD, // mod

    EQ, // eq
    EQUAL, // =
    EQUAL_EQUAL, // ==
    EMPTY, // empty
    QUESTION, // ?
    COLON, // :
    COMMA, // ,
    SEMICOLON, // ;
    PAREN_LEFT,  // (
    PAREN_RIGHT, // )
    ARROW, // ->

    BANG, // !
    BANG_EQUAL, // !=
    ANGLE_LEFT, // <
    ANGLE_LEFT_EQUAL, // <=
    ANGLE_RIGHT, // >
    ANGLE_RIGHT_EQUAL, // >=
    GE, // ge
    GT, // gt
    NE, // ne
    LE, // le
    LT, // lt

    AMP_AMP, // &&
    AND, // and
    BAR_BAR, // ||
    OR, // or
    NOT, // not

    DOLLAR, // $
    HASH, // #
    CURLY_LEFT, // {
    CURLY_RIGHT, // }
    SQUARE_LEFT, // [
    SQUARE_RIGHT, // ]
    DOT, // .

    IDENTIFIER,
    BOOLEAN,
    INTEGER,
    FLOAT,
    STRING,
    NULL;
}