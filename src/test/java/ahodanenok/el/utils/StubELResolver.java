package ahodanenok.el.utils;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;

public class StubELResolver extends ELResolver {

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        throw new IllegalStateException("Unexpected call to method 'getCommonPropertyType'");
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        throw new IllegalStateException("Unexpected call to method 'getType'");
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        throw new IllegalStateException("Unexpected call to method 'getValue'");
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        throw new IllegalStateException("Unexpected call to method 'isReadOnly'");
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        throw new IllegalStateException("Unexpected call to method 'setValue'");
    }
}
