package ahodanenok.el.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class LookaheadTokenizerTest {

    @Test
    public void testLookaheadAtTheStart() {
        LookaheadTokenizer tokenizer = new LookaheadTokenizer(
            new Tokenizer(new StringReader("10 20 30 40 50")), 4);
        assertEquals(Long.valueOf(10), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(20), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(3).getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(4).getValue());
        assertEquals(Long.valueOf(10), tokenizer.next().getValue());
        assertEquals(Long.valueOf(20), tokenizer.next().getValue());
        assertEquals(Long.valueOf(30), tokenizer.next().getValue());
        assertEquals(Long.valueOf(40), tokenizer.next().getValue());
        assertEquals(Long.valueOf(50), tokenizer.next().getValue());
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testLookaheadInTheMiddle() {
        LookaheadTokenizer tokenizer = new LookaheadTokenizer(
            new Tokenizer(new StringReader("10 20 30 40 50")), 4);
        assertEquals(Long.valueOf(10), tokenizer.next().getValue());
        assertEquals(Long.valueOf(20), tokenizer.next().getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(30), tokenizer.next().getValue());
        assertEquals(Long.valueOf(40), tokenizer.next().getValue());
        assertEquals(Long.valueOf(50), tokenizer.next().getValue());
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testLookaheadAtTheEnd() {
        LookaheadTokenizer tokenizer = new LookaheadTokenizer(
            new Tokenizer(new StringReader("10 20 30 40 50")), 4);
        assertEquals(Long.valueOf(10), tokenizer.next().getValue());
        assertEquals(Long.valueOf(20), tokenizer.next().getValue());
        assertEquals(Long.valueOf(30), tokenizer.next().getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(50), tokenizer.peek(2).getValue());
        assertNull(tokenizer.peek(3));
        assertEquals(Long.valueOf(40), tokenizer.next().getValue());
        assertEquals(Long.valueOf(50), tokenizer.next().getValue());
        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testLookaheadLimitExceeded() {
        LookaheadTokenizer tokenizer = new LookaheadTokenizer(
            new Tokenizer(new StringReader("10 20 30 40 50")), 4);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> tokenizer.peek(5));
        assertEquals("Lookahead count of 5 is greater than the limit of 4", ex.getMessage());
    }

    @Test
    public void testLookaheadRepeated() {
        LookaheadTokenizer tokenizer = new LookaheadTokenizer(
            new Tokenizer(new StringReader("10 20 30 40 50")), 3);

        assertEquals(Long.valueOf(10), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(20), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(3).getValue());
        assertEquals(Long.valueOf(10), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(20), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(3).getValue());

        assertEquals(Long.valueOf(10), tokenizer.next().getValue());
        assertEquals(Long.valueOf(20), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(3).getValue());
        assertEquals(Long.valueOf(20), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(3).getValue());

        assertEquals(Long.valueOf(20), tokenizer.next().getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(50), tokenizer.peek(3).getValue());
        assertEquals(Long.valueOf(30), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(2).getValue());
        assertEquals(Long.valueOf(50), tokenizer.peek(3).getValue());

        assertEquals(Long.valueOf(30), tokenizer.next().getValue());
        assertEquals(Long.valueOf(40), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(50), tokenizer.peek(2).getValue());
        assertNull(tokenizer.peek(3));
        assertEquals(Long.valueOf(40), tokenizer.peek(1).getValue());
        assertEquals(Long.valueOf(50), tokenizer.peek(2).getValue());
        assertNull(tokenizer.peek(3));

        assertEquals(Long.valueOf(40), tokenizer.next().getValue());
        assertEquals(Long.valueOf(50), tokenizer.next().getValue());
        assertFalse(tokenizer.hasNext());
    }
}
