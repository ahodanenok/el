package ahodanenok.el.expression;

import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;

public class IdentifierValueExpression extends ValueExpressionBase {

    final String name;
    final ValueExpression varExpr;

    IdentifierValueExpression(String name, ValueExpression varExpr) {
        this.name = name;
        this.varExpr = varExpr;
    }

    @Override
    public Class<?> getType(ELContext context) {
        if (context.isLambdaArgument(name)) {
            return null;
        } else if (varExpr != null) {
            return varExpr.getType(context);
        } else {
            Class<?> type = context.getELResolver().getType(context, null, name);
            if (!context.isPropertyResolved()) {
                throw new PropertyNotFoundException("todo");
            }

            return type;
        }
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        if (context.isLambdaArgument(name)) {
            return true;
        } else if (varExpr != null) {
            return varExpr.isReadOnly(context);
        } else {
            boolean readOnly = context.getELResolver().isReadOnly(context, null, name);
            if (!context.isPropertyResolved()) {
                // todo: static field
                throw new PropertyNotFoundException("todo");
            }

            return readOnly;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(ELContext context) {
        Object value;
        if (context.isLambdaArgument(name)) {
            value = context.getLambdaArgument(name);
        } else if (varExpr != null) {
            return varExpr.getValue(context);
        } else {
            value = context.getELResolver().getValue(context, null, name);
            if (!context.isPropertyResolved()) {
                // todo: static field
                throw new PropertyNotFoundException("todo");
            }
        }

        return (T) convertIfNecessary(context, value);
    }

    @Override
    public void setValue(ELContext context, Object value) {
        if (context.isLambdaArgument(name)) {
            throw new PropertyNotWritableException("'%s' is a lambda argument".formatted(name));
        } else if (varExpr != null) {
            varExpr.setValue(context, value);
        } else {
            context.getELResolver().setValue(context, null, name, value);
            if (!context.isPropertyResolved()) {
                // todo: static field
                throw new PropertyNotFoundException("todo");
            }
        }
    }

    @Override
    public ValueReference getValueReference(ELContext context) {
        if (varExpr != null) {
            return varExpr.getValueReference(context);
        } else {
            return new ValueReference(null, name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.hashCode(), varExpr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != IdentifierValueExpression.class) {
            return false;
        }

        IdentifierValueExpression other = (IdentifierValueExpression) obj;
        return name.equals(other.name) && Objects.equals(varExpr, other.varExpr);
    }
}
