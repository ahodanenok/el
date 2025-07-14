package ahodanenok.el.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;

public class EmptyValueExpressionTest {
    
    @Test
    public void testEmpty_Null() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(null));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testEmpty_EmptyString() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(""));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testEmpty_NonEmptyString() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression("test"));
        assertEquals(false, expr.getValue(context));
    }

    @Test
    public void testEmpty_EmptyArray() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(new int[0]));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testEmpty_NonEmptyArray() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(new int[] { 1 }));
        assertEquals(false, expr.getValue(context));
    }

    @Test
    public void testEmpty_EmptyMap() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(Map.of()));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testEmpty_NonEmptyMap() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(Map.of(1, 2)));
        assertEquals(false, expr.getValue(context));
    }

    @Test
    public void testEmpty_EmptyCollection() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(List.of()));
        assertEquals(true, expr.getValue(context));
    }

    @Test
    public void testEmpty_NonEmptyCollection() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(List.of(1)));
        assertEquals(false, expr.getValue(context));
    }

    @Test
    public void testEmpty_Other() {
        ExpressionFactory ef = ExpressionFactoryStubs.NONE;
        StandardELContext context = new StandardELContext(ef);
        context.putContext(ExpressionFactory.class, ef);
        var expr = new EmptyValueExpression(new StaticValueExpression(123));
        assertEquals(false, expr.getValue(context));
    }
}
