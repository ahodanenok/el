package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.StringReader;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ahodanenok.el.token.Tokenizer;

public class ParserTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "${!true}",
        "${not true}",
        "#{!true}",
        "#{not true}"
    })
    public void testParse_Not(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        NotValueExpression not = assertInstanceOf(NotValueExpression.class, parser.parseValue());
        StaticValueExpression bool = assertInstanceOf(StaticValueExpression.class, not.expr);
        assertEquals(true, bool.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${!!!0}",
        "${not not not 0}",
        "#{!!!0}",
        "#{not not not 0}"
    })
    public void testParse_Not_Nested(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        NotValueExpression not1 = assertInstanceOf(NotValueExpression.class, parser.parseValue());
        NotValueExpression not2 = assertInstanceOf(NotValueExpression.class, not1.expr);
        NotValueExpression not3 = assertInstanceOf(NotValueExpression.class, not2.expr);
        StaticValueExpression num = assertInstanceOf(StaticValueExpression.class, not3.expr);
        assertEquals(0L, num.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${empty 'abc'}",
        "#{empty 'abc'}",
    })
    public void testParse_Empty(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        EmptyValueExpression empty = assertInstanceOf(EmptyValueExpression.class, parser.parseValue());
        StaticValueExpression str = assertInstanceOf(StaticValueExpression.class, empty.expr);
        assertEquals("abc", str.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${empty empty empty null}",
        "#{empty empty empty null}",
    })
    public void testParse_Empty_Nested(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        EmptyValueExpression empty1 = assertInstanceOf(EmptyValueExpression.class, parser.parseValue());
        EmptyValueExpression empty2 = assertInstanceOf(EmptyValueExpression.class, empty1.expr);
        EmptyValueExpression empty3 = assertInstanceOf(EmptyValueExpression.class, empty2.expr);
        StaticValueExpression none = assertInstanceOf(StaticValueExpression.class, empty3.expr);
        assertEquals(null, none.value);
    }
}
