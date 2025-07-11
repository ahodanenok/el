package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import jakarta.el.ELProcessor;

public class EvalTest {

    @Test
    public void testEval_Null() {
        ELProcessor processor = new ELProcessor();
        assertNull(processor.eval("null"));
    }

    @Test
    public void testEval_String() {
        ELProcessor processor = new ELProcessor();
        assertEquals("hello!", processor.eval("'hello!'"));
        assertEquals("hello!", processor.eval("\"hello!\""));
    }

    @Test
    public void testEval_Boolean() {
        ELProcessor processor = new ELProcessor();
        assertEquals(true, processor.eval("true"));
        assertEquals(false, processor.eval("false"));
    }

    @Test
    public void testEval_Integer() {
        ELProcessor processor = new ELProcessor();
        assertEquals(Long.valueOf(0), processor.eval("0"));
        // assertEquals(Long.valueOf(-100), processor.eval("-100"));
        assertEquals(Long.valueOf(100), processor.eval("100"));
        assertEquals(Long.valueOf(Integer.MAX_VALUE), processor.eval("2147483647"));
        assertEquals(Long.valueOf(Long.MAX_VALUE), processor.eval("9223372036854775807"));
    }

    @Test
    public void testEval_Float() {
        ELProcessor processor = new ELProcessor();
        assertEquals(Double.valueOf(0), processor.eval("0.0"));
        // assertEquals(Double.valueOf(-100), processor.eval("-100.0"));
        assertEquals(Double.valueOf(100.123), processor.eval("100.123"));
        assertEquals(Double.valueOf(0.5352), processor.eval("0.5352"));
        assertEquals(Double.valueOf(0.5352), processor.eval(".5352"));
        assertEquals(Double.valueOf(0.5352), processor.eval("53.52e-2"));
        assertEquals(Double.valueOf(0.5352), processor.eval("0.005352e+2"));
    }
}
