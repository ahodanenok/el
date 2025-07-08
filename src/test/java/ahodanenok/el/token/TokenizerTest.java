package ahodanenok.el.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TokenizerTest {

    @Test
    public void testEmptyString() {
        Tokenizer tokenizer = new Tokenizer(new StringReader(""));
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testWhitespacesOnly() {
        Tokenizer tokenizer = new Tokenizer(new StringReader(" \n\r\t"));
        assertFalse(tokenizer.hasNext());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        +, PLUS
        +=, PLUS_EQUAL
        -, MINUS
        *, STAR
        /, SLASH
        %, PERCENT
        =, EQUAL
        ==, EQUAL_EQUAL
        ?, QUESTION
        :, COLON
        ;, SEMICOLON
        (, PAREN_LEFT
        ), PAREN_RIGHT
        ->, ARROW
        !, BANG
        !=, BANG_EQUAL
        <, ANGLE_LEFT
        <=, ANGLE_LEFT_EQUAL
        >, ANGLE_RIGHT
        >=, ANGLE_RIGHT_EQUAL
        &&, AMP_AMP
        $, DOLLAR
        #, HASH
        {, CURLY_LEFT
        }, CURLY_RIGHT
        [, SQUARE_LEFT
        ], SQUARE_RIGHT
        div, DIV
        mod, MOD
        eq, EQ
        empty, EMPTY
        ge, GE
        gt, GT
        ne, NE
        le, LE
        lt, LT
        and, AND
        or, OR
        not, NOT
        null, NULL
        """)
    public void testReadSymbols(String code, TokenType expectedType) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(code));
        assertTrue(tokenizer.hasNext());
        checkToken(tokenizer.next(), expectedType, code);
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testReadBoolean_True() {
        Tokenizer tokenizer = new Tokenizer(new StringReader("true"));
        assertTrue(tokenizer.hasNext());
        checkToken(tokenizer.next(), TokenType.BOOLEAN, "true", true);
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testReadBoolean_False() {
        Tokenizer tokenizer = new Tokenizer(new StringReader("false"));
        assertTrue(tokenizer.hasNext());
        checkToken(tokenizer.next(), TokenType.BOOLEAN, "false", false);
        assertFalse(tokenizer.hasNext());
    }

    // 123, INTEGER
    // 456.789, FLOAT,
    // \"hello, world\", STRING

    private void checkToken(Token token, TokenType expectedType, String expectedLexeme) {
        assertEquals(expectedType, token.getType());
        assertEquals(expectedLexeme, token.getLexeme());
        assertNull(token.getValue());
    }

    private void checkToken(Token token, TokenType expectedType, String expectedLexeme, Object expectedValue) {
        assertEquals(expectedType, token.getType());
        assertEquals(expectedLexeme, token.getLexeme());
        assertEquals(expectedValue, token.getValue());
    }
}
