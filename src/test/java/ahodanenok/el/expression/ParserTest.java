package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.StringReader;

import org.junit.jupiter.api.Test;
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

    @ParameterizedTest
    @ValueSource(strings = {
        "${-true}",
        "#{-true}",
    })
    public void testParse_Negate(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        NegateValueExpression neg = assertInstanceOf(NegateValueExpression.class, parser.parseValue());
        StaticValueExpression bool = assertInstanceOf(StaticValueExpression.class, neg.expr);
        assertEquals(true, bool.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${---false}",
        "#{---false}",
    })
    public void testParse_Negate_Nested(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        NegateValueExpression neg1 = assertInstanceOf(NegateValueExpression.class, parser.parseValue());
        NegateValueExpression neg2 = assertInstanceOf(NegateValueExpression.class, neg1.expr);
        NegateValueExpression neg3 = assertInstanceOf(NegateValueExpression.class, neg2.expr);
        StaticValueExpression bool = assertInstanceOf(StaticValueExpression.class, neg3.expr);
        assertEquals(false, bool.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 * 2}",
        "#{1 * 2}",
    })
    public void testParse_Multiply(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        MultiplyValueExpression div = assertInstanceOf(MultiplyValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, div.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, div.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 * 2 * 3 * 4}",
        "#{1 * 2 * 3 * 4}",
    })
    public void testParse_Multiply_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        MultiplyValueExpression mul1 = assertInstanceOf(MultiplyValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, mul1.right);
        assertEquals(4L, right1.value);

        MultiplyValueExpression mul2 = assertInstanceOf(MultiplyValueExpression.class, mul1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, mul2.right);
        assertEquals(3L, right2.value);

        MultiplyValueExpression mul3 = assertInstanceOf(MultiplyValueExpression.class, mul2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, mul3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, mul3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 / 2}",
        "${1 div 2}",
        "#{1 / 2}",
        "#{1 div 2}",
    })
    public void testParse_Divide(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        DivideValueExpression div = assertInstanceOf(DivideValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, div.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, div.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 / 2 / 3 / 4}",
        "${1 div 2 div 3 div 4}",
        "#{1 / 2 / 3 / 4}",
        "#{1 div 2 div 3 div 4}",
    })
    public void testParse_Divide_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        DivideValueExpression div1 = assertInstanceOf(DivideValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, div1.right);
        assertEquals(4L, right1.value);

        DivideValueExpression div2 = assertInstanceOf(DivideValueExpression.class, div1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, div2.right);
        assertEquals(3L, right2.value);

        DivideValueExpression div3 = assertInstanceOf(DivideValueExpression.class, div2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, div3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, div3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 % 2}",
        "${1 mod 2}",
        "#{1 % 2}",
        "#{1 mod 2}",
    })
    public void testParse_Modulo(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        ModuloValueExpression div = assertInstanceOf(ModuloValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, div.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, div.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 % 2 % 3 % 4}",
        "${1 mod 2 mod 3 mod 4}",
        "#{1 % 2 % 3 % 4}",
        "#{1 mod 2 mod 3 mod 4}",
    })
    public void testParse_Modulo_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        ModuloValueExpression mod1 = assertInstanceOf(ModuloValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, mod1.right);
        assertEquals(4L, right1.value);

        ModuloValueExpression mod2 = assertInstanceOf(ModuloValueExpression.class, mod1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, mod2.right);
        assertEquals(3L, right2.value);

        ModuloValueExpression mod3 = assertInstanceOf(ModuloValueExpression.class, mod2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, mod3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, mod3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 + 2}",
        "#{1 + 2}",
    })
    public void testParse_Add(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, add.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, add.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 + 2 + 3 + 4}",
        "#{1 + 2 + 3 + 4}",
    })
    public void testParse_Add_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        AddValueExpression add1 = assertInstanceOf(AddValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, add1.right);
        assertEquals(4L, right1.value);

        AddValueExpression add2 = assertInstanceOf(AddValueExpression.class, add1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, add2.right);
        assertEquals(3L, right2.value);

        AddValueExpression add3 = assertInstanceOf(AddValueExpression.class, add2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, add3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, add3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 - 2}",
        "#{1 - 2}",
    })
    public void testParse_Subtract(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        SubtractValueExpression sub = assertInstanceOf(SubtractValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, sub.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, sub.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 - 2 - 3 - 4}",
        "#{1 - 2 - 3 - 4}",
    })
    public void testParse_Subtract_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        SubtractValueExpression sub1 = assertInstanceOf(SubtractValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, sub1.right);
        assertEquals(4L, right1.value);

        SubtractValueExpression sub2 = assertInstanceOf(SubtractValueExpression.class, sub1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, sub2.right);
        assertEquals(3L, right2.value);

        SubtractValueExpression sub3 = assertInstanceOf(SubtractValueExpression.class, sub2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, sub3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, sub3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 += 2}",
        "#{1 += 2}",
    })
    public void testParse_Concatenate(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        ConcatenateValueExpression div = assertInstanceOf(ConcatenateValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, div.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, div.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 += 2 += 3 += 4}",
        "#{1 += 2 += 3 += 4}",
    })
    public void testParse_Concatenate_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        ConcatenateValueExpression concat1 = assertInstanceOf(ConcatenateValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, concat1.right);
        assertEquals(4L, right1.value);

        ConcatenateValueExpression concat2 = assertInstanceOf(ConcatenateValueExpression.class, concat1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, concat2.right);
        assertEquals(3L, right2.value);

        ConcatenateValueExpression concat3 = assertInstanceOf(ConcatenateValueExpression.class, concat2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, concat3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, concat3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 < 2}",
        "${1 lt 2}",
        "#{1 < 2}",
        "#{1 lt 2}",
    })
    public void testParse_LessThan(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        LessThanValueExpression lt = assertInstanceOf(LessThanValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, lt.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, lt.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 < 2 < 3 < 4}",
        "${1 lt 2 lt 3 lt 4}",
        "#{1 < 2 < 3 < 4}",
        "#{1 lt 2 lt 3 lt 4}",
    })
    public void testParse_LessThan_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        LessThanValueExpression lt1 = assertInstanceOf(LessThanValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, lt1.right);
        assertEquals(4L, right1.value);

        LessThanValueExpression lt2 = assertInstanceOf(LessThanValueExpression.class, lt1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, lt2.right);
        assertEquals(3L, right2.value);

        LessThanValueExpression lt3 = assertInstanceOf(LessThanValueExpression.class, lt2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, lt3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, lt3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 <= 2}",
        "${1 le 2}",
        "#{1 <= 2}",
        "#{1 le 2}"
    })
    public void testParse_LessEqual(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        LessEqualValueExpression lt = assertInstanceOf(LessEqualValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, lt.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, lt.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 <= 2 <= 3 <= 4}",
        "${1 le 2 le 3 le 4}",
        "#{1 <= 2 <= 3 <= 4}",
        "#{1 le 2 le 3 le 4}"
    })
    public void testParse_LessEqual_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        LessEqualValueExpression le1 = assertInstanceOf(LessEqualValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, le1.right);
        assertEquals(4L, right1.value);

        LessEqualValueExpression le2 = assertInstanceOf(LessEqualValueExpression.class, le1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, le2.right);
        assertEquals(3L, right2.value);

        LessEqualValueExpression le3 = assertInstanceOf(LessEqualValueExpression.class, le2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, le3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, le3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 > 2}",
        "${1 gt 2}",
        "#{1 > 2}",
        "#{1 gt 2}",
    })
    public void testParse_GreaterThan(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));
        GreaterThanValueExpression gt = assertInstanceOf(GreaterThanValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, gt.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, gt.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 > 2 > 3 > 4}",
        "${1 gt 2 gt 3 gt 4}",
        "#{1 > 2 > 3 > 4}",
        "#{1 gt 2 gt 3 gt 4}"
    })
    public void testParse_GreaterThan_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)));

        GreaterThanValueExpression gt1 = assertInstanceOf(GreaterThanValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, gt1.right);
        assertEquals(4L, right1.value);

        GreaterThanValueExpression gt2 = assertInstanceOf(GreaterThanValueExpression.class, gt1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, gt2.right);
        assertEquals(3L, right2.value);

        GreaterThanValueExpression gt3 = assertInstanceOf(GreaterThanValueExpression.class, gt2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, gt3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, gt3.right);
        assertEquals(2L, right3.value);
    }
}
