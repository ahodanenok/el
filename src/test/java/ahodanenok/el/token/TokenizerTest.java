package ahodanenok.el.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TokenizerTest {

    @Test
    public void testEmptyString() {
        Tokenizer tokenizer = new Tokenizer(new StringReader(""));
        assertFalse(tokenizer.hasNext());
        NoSuchElementException ex = assertThrows(
            NoSuchElementException.class, () -> tokenizer.next());
        assertEquals("No more tokens", ex.getMessage());
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
        ',', COMMA
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

    @ParameterizedTest
    @CsvSource(textBlock = """
        0, 0
        1, 1
        35, 35
        123, 123
        9293, 9293
        1299522, 1299522
        2147483647, 2147483647
        9223372036854775807, 9223372036854775807
        """)
    public void testReadInteger(String code, long expectedNumber) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(code));
        assertTrue(tokenizer.hasNext());
        checkToken(tokenizer.next(), TokenType.INTEGER, code, expectedNumber);
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testReadInteger_TooLarge() {
        Tokenizer tokenizer = new Tokenizer(new StringReader("9223372036854775808"));
        assertTrue(tokenizer.hasNext());
        IllegalStateException ex = assertThrows(
            IllegalStateException.class, () -> tokenizer.next());
        assertEquals("Integer literal '9223372036854775808' is too large", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        0.0, 0.0
        0.0e0, 0.0
        0.0e+0, 0.0
        0.0e-0, 0.0
        1.5, 1.5
        372.53, 372.53
        4232.51232321, 4232.51232321
        123.56e+3, 123560.0
        123.56e3, 123560.0
        123.56E+3, 123560.0
        123.56E3, 123560.0
        25256.23e-4, 2.525623
        25256.23E-4, 2.525623
        .0, 0.0
        .1, 0.1
        .94982234, 0.94982234
        .2326e+5, 23260
        .2326e5, 23260
        .2326E+5, 23260
        .2326E5, 23260
        .673e-2, 0.00673
        .673E-2, 0.00673
        """)
    public void testReadFloat(String code, double expectedNumber) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(code));
        assertTrue(tokenizer.hasNext());
        checkToken(tokenizer.next(), TokenType.FLOAT, code, expectedNumber);
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testReadFloat_InvalidExponent() {
        Tokenizer tokenizer = new Tokenizer(new StringReader("10.5E"));
        assertTrue(tokenizer.hasNext());
        IllegalStateException ex = assertThrows(
            IllegalStateException.class, () -> tokenizer.next());
        assertEquals("Invalid float literal '10.5E'", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '$', textBlock = """
        '', $$
        'a', a
        'test', test
        $'Hello, World!'$, $Hello, World!$
        "", $$
        "a", a
        "test", test
        $"Hello, World!"$, $Hello, World!$
        """)
    public void testReadString(String code, String expectedString) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(code));
        assertTrue(tokenizer.hasNext());
        checkToken(tokenizer.next(), TokenType.STRING, code, expectedString);
        assertFalse(tokenizer.hasNext());
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '$', textBlock = """
        '\\\\', $'\\'$, $\\$
        '\\\'', $'\''$, $'$
        "\\\\", $"\\"$, $\\$
        "\\\"", $"\""$, $"$
        """)
    public void testReadString_Escapes(String code, String expectedLexeme, String expectedString) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(code));
        assertTrue(tokenizer.hasNext());
        checkToken(tokenizer.next(), TokenType.STRING, expectedLexeme, expectedString);
        assertFalse(tokenizer.hasNext());
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '$', textBlock = """
        '\\n', $\\n$
        '\\"', $\\"$
        "\\'", $\\'$
        """)
    public void testReadString_IllegalEscape(String code, String expectedSequence) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(code));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> tokenizer.next());
        assertEquals("Unsupported escape sequence '%s'".formatted(expectedSequence), ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "\"abc", "'abc" })
    public void testReadString_Unclosed(String code) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(code));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> tokenizer.next());
        assertEquals("Unclosed string literal", ex.getMessage());
    }

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
