package ahodanenok.el.utils;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.FunctionMapper;
import jakarta.el.VariableMapper;

public class StubELContext extends ELContext {

    @Override
    public ELResolver getELResolver() {
        throw new IllegalStateException("Unexpected call to method 'getELResolver'");
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        throw new IllegalStateException("Unexpected call to method 'getFunctionMapper'");
    }

    @Override
    public VariableMapper getVariableMapper() {
        throw new IllegalStateException("Unexpected call to method 'getVariableMapper'");
    }
}
