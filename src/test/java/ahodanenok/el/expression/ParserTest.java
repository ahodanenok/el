package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.StringReader;
import java.lang.reflect.Method;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ahodanenok.el.token.Tokenizer;
import ahodanenok.el.utils.StubELContext;
import jakarta.el.FunctionMapper;
import jakarta.el.StandardELContext;
import jakarta.el.VariableMapper;

public class ParserTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "${!true}",
        "${not true}",
        "#{!true}",
        "#{not true}"
    })
    public void testParse_Not(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
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
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

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

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 >= 2}",
        "${1 ge 2}",
        "#{1 >= 2}",
        "#{1 ge 2}",
    })
    public void testParse_GreaterEqual(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        GreaterEqualValueExpression ge = assertInstanceOf(GreaterEqualValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, ge.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, ge.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 >= 2 >= 3 >= 4}",
        "${1 ge 2 ge 3 ge 4}",
        "#{1 >= 2 >= 3 >= 4}",
        "#{1 ge 2 ge 3 ge 4}"
    })
    public void testParse_GreaterEqual_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        GreaterEqualValueExpression ge1 = assertInstanceOf(GreaterEqualValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, ge1.right);
        assertEquals(4L, right1.value);

        GreaterEqualValueExpression ge2 = assertInstanceOf(GreaterEqualValueExpression.class, ge1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, ge2.right);
        assertEquals(3L, right2.value);

        GreaterEqualValueExpression ge3 = assertInstanceOf(GreaterEqualValueExpression.class, ge2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, ge3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, ge3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 == 2}",
        "${1 eq 2}",
        "#{1 == 2}",
        "#{1 eq 2}",
    })
    public void testParse_Equal(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        EqualValueExpression eq = assertInstanceOf(EqualValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, eq.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, eq.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 == 2 == 3 == 4}",
        "${1 eq 2 eq 3 eq 4}",
        "#{1 == 2 == 3 == 4}",
        "#{1 eq 2 eq 3 eq 4}"
    })
    public void testParse_Equal_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        EqualValueExpression eq1 = assertInstanceOf(EqualValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, eq1.right);
        assertEquals(4L, right1.value);

        EqualValueExpression eq2 = assertInstanceOf(EqualValueExpression.class, eq1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, eq2.right);
        assertEquals(3L, right2.value);

        EqualValueExpression eq3 = assertInstanceOf(EqualValueExpression.class, eq2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, eq3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, eq3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 != 2}",
        "${1 ne 2}",
        "#{1 != 2}",
        "#{1 ne 2}",
    })
    public void testParse_NotEqual(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        NotEqualValueExpression eq = assertInstanceOf(NotEqualValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, eq.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, eq.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 != 2 != 3 != 4}",
        "${1 ne 2 ne 3 ne 4}",
        "#{1 != 2 != 3 != 4}",
        "#{1 ne 2 ne 3 ne 4}"
    })
    public void testParse_NotEqual_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        NotEqualValueExpression ne1 = assertInstanceOf(NotEqualValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, ne1.right);
        assertEquals(4L, right1.value);

        NotEqualValueExpression ne2 = assertInstanceOf(NotEqualValueExpression.class, ne1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, ne2.right);
        assertEquals(3L, right2.value);

        NotEqualValueExpression ne3 = assertInstanceOf(NotEqualValueExpression.class, ne2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, ne3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, ne3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 && 2}",
        "${1 and 2}",
        "#{1 && 2}",
        "#{1 and 2}",
    })
    public void testParse_And(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        AndValueExpression and = assertInstanceOf(AndValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, and.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, and.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 && 2 && 3 && 4}",
        "${1 and 2 and 3 and 4}",
        "#{1 && 2 && 3 && 4}",
        "#{1 and 2 and 3 and 4}"
    })
    public void testParse_And_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        AndValueExpression and1 = assertInstanceOf(AndValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, and1.right);
        assertEquals(4L, right1.value);

        AndValueExpression and2 = assertInstanceOf(AndValueExpression.class, and1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, and2.right);
        assertEquals(3L, right2.value);

        AndValueExpression and3 = assertInstanceOf(AndValueExpression.class, and2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, and3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, and3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 || 2}",
        "${1 or 2}",
        "#{1 || 2}",
        "#{1 or 2}",
    })
    public void testParse_Or(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        OrValueExpression and = assertInstanceOf(OrValueExpression.class, parser.parseValue());
        StaticValueExpression left = assertInstanceOf(StaticValueExpression.class, and.left);
        assertEquals(1L, left.value);
        StaticValueExpression right = assertInstanceOf(StaticValueExpression.class, and.right);
        assertEquals(2L, right.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 || 2 || 3 || 4}",
        "${1 or 2 or 3 or 4}",
        "#{1 || 2 || 3 || 4}",
        "#{1 or 2 or 3 or 4}"
    })
    public void testParse_Or_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        OrValueExpression or1 = assertInstanceOf(OrValueExpression.class, parser.parseValue());
        StaticValueExpression right1 = assertInstanceOf(StaticValueExpression.class, or1.right);
        assertEquals(4L, right1.value);

        OrValueExpression or2 = assertInstanceOf(OrValueExpression.class, or1.left);
        StaticValueExpression right2 = assertInstanceOf(StaticValueExpression.class, or2.right);
        assertEquals(3L, right2.value);

        OrValueExpression or3 = assertInstanceOf(OrValueExpression.class, or2.left);
        StaticValueExpression left3 = assertInstanceOf(StaticValueExpression.class, or3.left);
        assertEquals(1L, left3.value);
        StaticValueExpression right3 = assertInstanceOf(StaticValueExpression.class, or3.right);
        assertEquals(2L, right3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 ? 2 : 3}",
        "#{1 ? 2 : 3}"
    })
    public void testParse_Conditinal(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        ConditionalValueExpression conditional = assertInstanceOf(ConditionalValueExpression.class, parser.parseValue());
        StaticValueExpression condition = assertInstanceOf(StaticValueExpression.class, conditional.condition);
        assertEquals(1L, condition.value);
        StaticValueExpression onTrue = assertInstanceOf(StaticValueExpression.class, conditional.onTrue);
        assertEquals(2L, onTrue.value);
        StaticValueExpression onFalse = assertInstanceOf(StaticValueExpression.class, conditional.onFalse);
        assertEquals(3L, onFalse.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 ? 2 : 3 ? 4 : 5 ? 6 : 7}",
        "#{1 ? 2 : 3 ? 4 : 5 ? 6 : 7}"
    })
    public void testParse_Conditinal_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        ConditionalValueExpression conditional1 = assertInstanceOf(ConditionalValueExpression.class, parser.parseValue());
        StaticValueExpression condition1 = assertInstanceOf(StaticValueExpression.class, conditional1.condition);
        assertEquals(1L, condition1.value);
        StaticValueExpression onTrue1 = assertInstanceOf(StaticValueExpression.class, conditional1.onTrue);
        assertEquals(2L, onTrue1.value);

        ConditionalValueExpression conditional2 = assertInstanceOf(ConditionalValueExpression.class, conditional1.onFalse);
        StaticValueExpression condition2 = assertInstanceOf(StaticValueExpression.class, conditional2.condition);
        assertEquals(3L, condition2.value);
        StaticValueExpression onTrue2 = assertInstanceOf(StaticValueExpression.class, conditional2.onTrue);
        assertEquals(4L, onTrue2.value);

        ConditionalValueExpression conditional3 = assertInstanceOf(ConditionalValueExpression.class, conditional2.onFalse);
        StaticValueExpression condition3 = assertInstanceOf(StaticValueExpression.class, conditional3.condition);
        assertEquals(5L, condition3.value);
        StaticValueExpression onTrue3 = assertInstanceOf(StaticValueExpression.class, conditional3.onTrue);
        assertEquals(6L, onTrue3.value);
        StaticValueExpression onFalse3 = assertInstanceOf(StaticValueExpression.class, conditional3.onFalse);
        assertEquals(7L, onFalse3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 ? 2 ? 3 : 4 : 5 ? 6 : 7}",
        "#{1 ? 2 ? 3 : 4 : 5 ? 6 : 7}"
    })
    public void testParse_Conditinal_Nested(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        ConditionalValueExpression conditional1 = assertInstanceOf(ConditionalValueExpression.class, parser.parseValue());
        StaticValueExpression condition1 = assertInstanceOf(StaticValueExpression.class, conditional1.condition);
        assertEquals(1L, condition1.value);

        ConditionalValueExpression conditional2 = assertInstanceOf(ConditionalValueExpression.class, conditional1.onTrue);
        StaticValueExpression condition2 = assertInstanceOf(StaticValueExpression.class, conditional2.condition);
        assertEquals(2L, condition2.value);
        StaticValueExpression onTrue2 = assertInstanceOf(StaticValueExpression.class, conditional2.onTrue);
        assertEquals(3L, onTrue2.value);
        StaticValueExpression onFalse2 = assertInstanceOf(StaticValueExpression.class, conditional2.onFalse);
        assertEquals(4L, onFalse2.value);

        ConditionalValueExpression conditional3 = assertInstanceOf(ConditionalValueExpression.class, conditional1.onFalse);
        StaticValueExpression condition3 = assertInstanceOf(StaticValueExpression.class, conditional3.condition);
        assertEquals(5L, condition3.value);
        StaticValueExpression onTrue3 = assertInstanceOf(StaticValueExpression.class, conditional3.onTrue);
        assertEquals(6L, onTrue3.value);
        StaticValueExpression onFalse3 = assertInstanceOf(StaticValueExpression.class, conditional3.onFalse);
        assertEquals(7L, onFalse3.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 ; 2}",
        "#{1 ; 2}",
    })
    public void testParse_Semicolon(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        SemicolonValueExpression sc = assertInstanceOf(SemicolonValueExpression.class, parser.parseValue());
        assertEquals(2, sc.expressions.size());
        StaticValueExpression expr1 = assertInstanceOf(StaticValueExpression.class, sc.expressions.get(0));
        assertEquals(1L, expr1.value);
        StaticValueExpression expr2 = assertInstanceOf(StaticValueExpression.class, sc.expressions.get(1));
        assertEquals(2L, expr2.value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${1 ; 2 ; 3 ; 4}",
        "#{1 ; 2 ; 3 ; 4}",
    })
    public void testParse_Semicolon_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        SemicolonValueExpression sc = assertInstanceOf(SemicolonValueExpression.class, parser.parseValue());
        assertEquals(4, sc.expressions.size());
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, sc.expressions.get(0)).value);
        assertEquals(2L, assertInstanceOf(StaticValueExpression.class, sc.expressions.get(1)).value);
        assertEquals(3L, assertInstanceOf(StaticValueExpression.class, sc.expressions.get(2)).value);
        assertEquals(4L, assertInstanceOf(StaticValueExpression.class, sc.expressions.get(3)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${(1 + 2)}",
        "#{(1 + 2)}",
    })
    public void testParse_Parenthesis(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        ParenthesisValueExpression p = assertInstanceOf(ParenthesisValueExpression.class, parser.parseValue());
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, p.expr);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.left).value);
        assertEquals(2L, assertInstanceOf(StaticValueExpression.class, add.right).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${(1 + ((2 + 3) + 4))}",
        "#{(1 + ((2 + 3) + 4))}",
    })
    public void testParse_Parenthesis_Nested(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        ParenthesisValueExpression p1 = assertInstanceOf(ParenthesisValueExpression.class, parser.parseValue());
        AddValueExpression add1 = assertInstanceOf(AddValueExpression.class, p1.expr);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add1.left).value);

        ParenthesisValueExpression p2 = assertInstanceOf(ParenthesisValueExpression.class, add1.right);
        AddValueExpression add2 = assertInstanceOf(AddValueExpression.class, p2.expr);
        assertEquals(4L, assertInstanceOf(StaticValueExpression.class, add2.right).value);

        ParenthesisValueExpression p3 = assertInstanceOf(ParenthesisValueExpression.class, add2.left);
        AddValueExpression add3 = assertInstanceOf(AddValueExpression.class, p3.expr);
        assertEquals(2L, assertInstanceOf(StaticValueExpression.class, add3.left).value);
        assertEquals(3L, assertInstanceOf(StaticValueExpression.class, add3.right).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${x}",
        "#{x}",
    })
    public void testParse_Identifier(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        assertEquals("x", assertInstanceOf(IdentifierValueExpression.class, parser.parseValue()).name);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a = 1}",
        "#{a = 1}",
    })
    public void testParse_Assignment(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        AssignValueExpression assign = assertInstanceOf(AssignValueExpression.class, parser.parseValue());
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, assign.left).name);
        assertEquals(Long.valueOf(1), assertInstanceOf(StaticValueExpression.class, assign.right).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a = b = c = 1}",
        "#{a = b = c = 1}",
    })
    public void testParse_Assign_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        AssignValueExpression assign1 = assertInstanceOf(AssignValueExpression.class, parser.parseValue());
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, assign1.left).name);

        AssignValueExpression assign2 = assertInstanceOf(AssignValueExpression.class, assign1.right);
        assertEquals("b", assertInstanceOf(IdentifierValueExpression.class, assign2.left).name);

        AssignValueExpression assign3 = assertInstanceOf(AssignValueExpression.class, assign2.right);
        assertEquals("c", assertInstanceOf(IdentifierValueExpression.class, assign3.left).name);
        assertEquals(Long.valueOf(1), assertInstanceOf(StaticValueExpression.class, assign3.right).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${x.y}",
        "#{x.y}",
    })
    public void testParse_PropertyAccess_Dot(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        PropertyAccessValueExpression prop = assertInstanceOf(PropertyAccessValueExpression.class, parser.parseValue());
        assertEquals("x", assertInstanceOf(IdentifierValueExpression.class, prop.baseExpr).name);
        assertEquals("y", assertInstanceOf(StaticValueExpression.class, prop.propertyExpr).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${x[y + 1]}",
        "#{x[y + 1]}",
    })
    public void testParse_PropertyAccess_Brackets(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));
        PropertyAccessValueExpression prop = assertInstanceOf(PropertyAccessValueExpression.class, parser.parseValue());
        assertEquals("x", assertInstanceOf(IdentifierValueExpression.class, prop.baseExpr).name);
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, prop.propertyExpr);
        assertEquals("y", assertInstanceOf(IdentifierValueExpression.class, add.left).name);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.right).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a.b.c.d}",
        "#{a.b.c.d}",
    })
    public void testParse_PropertyAccess_Dot_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        PropertyAccessValueExpression prop_1 = assertInstanceOf(PropertyAccessValueExpression.class, parser.parseValue());
        assertEquals("d", assertInstanceOf(StaticValueExpression.class, prop_1.propertyExpr).value);

        PropertyAccessValueExpression prop_2 = assertInstanceOf(PropertyAccessValueExpression.class, prop_1.baseExpr);
        assertEquals("c", assertInstanceOf(StaticValueExpression.class, prop_2.propertyExpr).value);

        PropertyAccessValueExpression prop_3 = assertInstanceOf(PropertyAccessValueExpression.class, prop_2.baseExpr);
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, prop_3.baseExpr).name);
        assertEquals("b", assertInstanceOf(StaticValueExpression.class, prop_3.propertyExpr).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a[0][c+1]['d']}",
        "#{a[0][c+1]['d']}",
    })
    public void testParse_PropertyAccess_Brackets_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        PropertyAccessValueExpression prop_1 = assertInstanceOf(PropertyAccessValueExpression.class, parser.parseValue());
        assertEquals("d", assertInstanceOf(StaticValueExpression.class, prop_1.propertyExpr).value);

        PropertyAccessValueExpression prop_2 = assertInstanceOf(PropertyAccessValueExpression.class, prop_1.baseExpr);
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, prop_2.propertyExpr);
        assertEquals("c", assertInstanceOf(IdentifierValueExpression.class, add.left).name);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.right).value);

        PropertyAccessValueExpression prop_3 = assertInstanceOf(PropertyAccessValueExpression.class, prop_2.baseExpr);
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, prop_3.baseExpr).name);
        assertEquals(0L, assertInstanceOf(StaticValueExpression.class, prop_3.propertyExpr).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a.b('x', 1)}",
        "#{a.b('x', 1)}",
    })
    public void testParse_PropertyCall_Dot(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        PropertyCallValueExpression call = assertInstanceOf(PropertyCallValueExpression.class, parser.parseValue());
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, call.left).name);
        assertEquals("b", assertInstanceOf(StaticValueExpression.class, call.right).value);
        assertEquals(2, call.params.size());
        assertEquals("x", assertInstanceOf(StaticValueExpression.class, call.params.get(0)).value);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, call.params.get(1)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a.b('x', 1).c().d(true, 'foo', 100)}",
        "#{a.b('x', 1).c().d(true, 'foo', 100)}",
    })
    public void testParse_PropertyCall_Dot_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        PropertyCallValueExpression call_1 = assertInstanceOf(PropertyCallValueExpression.class, parser.parseValue());
        assertEquals("d", assertInstanceOf(StaticValueExpression.class, call_1.right).value);
        assertEquals(3, call_1.params.size());
        assertEquals(true, assertInstanceOf(StaticValueExpression.class, call_1.params.get(0)).value);
        assertEquals("foo", assertInstanceOf(StaticValueExpression.class, call_1.params.get(1)).value);
        assertEquals(100L, assertInstanceOf(StaticValueExpression.class, call_1.params.get(2)).value);

        PropertyCallValueExpression call_2 = assertInstanceOf(PropertyCallValueExpression.class, call_1.left);
        assertEquals("c", assertInstanceOf(StaticValueExpression.class, call_2.right).value);
        assertEquals(0, call_2.params.size());

        PropertyCallValueExpression call_3 = assertInstanceOf(PropertyCallValueExpression.class, call_2.left);
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, call_3.left).name);
        assertEquals("b", assertInstanceOf(StaticValueExpression.class, call_3.right).value);
        assertEquals(2, call_3.params.size());
        assertEquals("x", assertInstanceOf(StaticValueExpression.class, call_3.params.get(0)).value);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, call_3.params.get(1)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a[b + 1]('x', 2)}",
        "#{a[b + 1]('x', 2)}",
    })
    public void testParse_PropertyCall_Brackets(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        PropertyCallValueExpression call = assertInstanceOf(PropertyCallValueExpression.class, parser.parseValue());
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, call.left).name);
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, call.right);
        assertEquals("b", assertInstanceOf(IdentifierValueExpression.class, add.left).name);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.right).value);
        assertEquals(2, call.params.size());
        assertEquals("x", assertInstanceOf(StaticValueExpression.class, call.params.get(0)).value);
        assertEquals(2L, assertInstanceOf(StaticValueExpression.class, call.params.get(1)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a[b + 1]('x', 2)['c']()[0]('k', 10, true)}",
        "#{a[b + 1]('x', 2)['c']()[0]('k', 10, true)}",
    })
    public void testParse_PropertyCall_Brackets_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        PropertyCallValueExpression call_1 = assertInstanceOf(PropertyCallValueExpression.class, parser.parseValue());
        assertEquals(0L, assertInstanceOf(StaticValueExpression.class, call_1.right).value);
        assertEquals(3, call_1.params.size());
        assertEquals("k", assertInstanceOf(StaticValueExpression.class, call_1.params.get(0)).value);
        assertEquals(10L, assertInstanceOf(StaticValueExpression.class, call_1.params.get(1)).value);
        assertEquals(true, assertInstanceOf(StaticValueExpression.class, call_1.params.get(2)).value);

        PropertyCallValueExpression call_2 = assertInstanceOf(PropertyCallValueExpression.class, call_1.left);
        assertEquals("c", assertInstanceOf(StaticValueExpression.class, call_2.right).value);
        assertEquals(0, call_2.params.size());

        PropertyCallValueExpression call_3 = assertInstanceOf(PropertyCallValueExpression.class, call_2.left);
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, call_3.left).name);
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, call_3.right);
        assertEquals("b", assertInstanceOf(IdentifierValueExpression.class, add.left).name);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.right).value);
        assertEquals(2, call_3.params.size());
        assertEquals("x", assertInstanceOf(StaticValueExpression.class, call_3.params.get(0)).value);
        assertEquals(2L, assertInstanceOf(StaticValueExpression.class, call_3.params.get(1)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a(b + 1, 'c')}",
        "#{a(b + 1, 'c')}",
    })
    public void testParse_FunctionCall(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        FunctionCallValueExpression call = assertInstanceOf(FunctionCallValueExpression.class, parser.parseValue());
        assertNull(call.prefix);
        assertEquals("a", call.localName);
        assertNull(call.variableExpr);
        assertNull(call.function);
        assertNull(call.mappedMethod);
        assertEquals(2, call.args.size());
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, call.args.get(0));
        assertEquals("b", assertInstanceOf(IdentifierValueExpression.class, add.left).name);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.right).value);
        assertEquals("c", assertInstanceOf(StaticValueExpression.class, call.args.get(1)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${math:max(1, 2)}",
        "#{math:max(1, 2)}",
    })
    public void testParse_FunctionCall_Prefixed(String code) throws Exception {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StubELContext() {
            @Override
            public VariableMapper getVariableMapper() {
                return null;
            }

            @Override
            public FunctionMapper getFunctionMapper() {
                return new FunctionMapper() {
                    @Override
                    public Method resolveFunction(String prefix, String localName) {
                        assertEquals("math", prefix);
                        assertEquals("max", localName);
                        try {
                            return Math.class.getMethod("max", long.class, long.class);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            }
        });

        FunctionCallValueExpression call = assertInstanceOf(FunctionCallValueExpression.class, parser.parseValue());
        assertEquals("math", call.prefix);
        assertEquals("max", call.localName);
        assertNull(call.variableExpr);
        assertNull(call.function);
        assertEquals(Math.class.getMethod("max", long.class, long.class), call.mappedMethod);
        assertEquals(2, call.args.size());
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, call.args.get(0)).value);
        assertEquals(2L, assertInstanceOf(StaticValueExpression.class, call.args.get(1)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${a(b + 1, 'c')()(true, 10, x)}",
        "#{a(b + 1, 'c')()(true, 10, x)}",
    })
    public void testParse_FunctionCall_Chain(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        FunctionCallValueExpression call_1 = assertInstanceOf(FunctionCallValueExpression.class, parser.parseValue());
        assertNull(call_1.prefix);
        assertNull(call_1.localName);
        assertNull(call_1.variableExpr);
        assertNull(call_1.mappedMethod);
        assertEquals(3, call_1.args.size());
        assertEquals(true, assertInstanceOf(StaticValueExpression.class, call_1.args.get(0)).value);
        assertEquals(10L, assertInstanceOf(StaticValueExpression.class, call_1.args.get(1)).value);
        assertEquals("x", assertInstanceOf(IdentifierValueExpression.class, call_1.args.get(2)).name);

        FunctionCallValueExpression call_2 = assertInstanceOf(FunctionCallValueExpression.class, call_1.function);
        assertNull(call_2.prefix);
        assertNull(call_2.localName);
        assertNull(call_2.variableExpr);
        assertNull(call_2.mappedMethod);
        assertEquals(0, call_2.args.size());

        FunctionCallValueExpression call_3 = assertInstanceOf(FunctionCallValueExpression.class, call_2.function);
        assertNull(call_3.prefix);
        assertEquals("a", call_3.localName);
        assertNull(call_3.function);
        assertNull(call_3.mappedMethod);
        assertEquals(2, call_3.args.size());
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, call_3.args.get(0));
        assertEquals("b", assertInstanceOf(IdentifierValueExpression.class, add.left).name);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.right).value);
        assertEquals("c", assertInstanceOf(StaticValueExpression.class, call_3.args.get(1)).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${() -> 'a'}",
        "#{() -> 'a'}",
    })
    public void testParse_Lambda_NoArgs(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        LambdaValueExpression lambda = assertInstanceOf(LambdaValueExpression.class, parser.parseValue());
        assertEquals(0, lambda.params.size());
        assertEquals("a", assertInstanceOf(StaticValueExpression.class, lambda.body).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${x -> x + 1}",
        "${(x) -> x + 1}",
        "#{x -> x + 1}",
        "#{(x) -> x + 1}",
    })
    public void testParse_Lambda_OneArg(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        LambdaValueExpression lambda = assertInstanceOf(LambdaValueExpression.class, parser.parseValue());
        assertEquals(1, lambda.params.size());
        assertEquals("x", lambda.params.get(0));
        AddValueExpression add = assertInstanceOf(AddValueExpression.class, lambda.body);
        assertEquals("x", assertInstanceOf(IdentifierValueExpression.class, add.left).name);
        assertEquals(1L, assertInstanceOf(StaticValueExpression.class, add.right).value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${(a, b, c) -> (a - b) / c}",
        "#{(a, b, c) -> (a - b) / c}",
    })
    public void testParse_Lambda_MultipleArgs(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        LambdaValueExpression lambda = assertInstanceOf(LambdaValueExpression.class, parser.parseValue());
        assertEquals(3, lambda.params.size());
        assertEquals("a", lambda.params.get(0));
        assertEquals("b", lambda.params.get(1));
        assertEquals("c", lambda.params.get(2));
        DivideValueExpression div = assertInstanceOf(DivideValueExpression.class, lambda.body);
        assertEquals("c", assertInstanceOf(IdentifierValueExpression.class, div.right).name);
        SubtractValueExpression sub = assertInstanceOf(SubtractValueExpression.class,
            assertInstanceOf(ParenthesisValueExpression.class, div.left).expr);
        assertEquals("a", assertInstanceOf(IdentifierValueExpression.class, sub.left).name);
        assertEquals("b", assertInstanceOf(IdentifierValueExpression.class, sub.right).name);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "${() -> a -> (b, c) -> true}",
        "#{() -> a -> (b, c) -> true}",
    })
    public void testParse_Lambda_Nested(String code) {
        Parser parser = new Parser(new Tokenizer(new StringReader(code)), new StandardELContext(ExpressionFactoryStubs.NONE));

        LambdaValueExpression lambda1 = assertInstanceOf(LambdaValueExpression.class, parser.parseValue());
        assertEquals(0, lambda1.params.size());

        LambdaValueExpression lambda2 = assertInstanceOf(LambdaValueExpression.class, lambda1.body);
        assertEquals(1, lambda2.params.size());
        assertEquals("a", lambda2.params.get(0));

        LambdaValueExpression lambda3 = assertInstanceOf(LambdaValueExpression.class, lambda2.body);
        assertEquals(2, lambda3.params.size());
        assertEquals("b", lambda3.params.get(0));
        assertEquals("c", lambda3.params.get(1));
        assertEquals(true, assertInstanceOf(StaticValueExpression.class, lambda3.body).value);
    }
}
