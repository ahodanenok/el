package ahodanenok.el.utils;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.FunctionMapper;
import jakarta.el.VariableMapper;

public class StubELContext extends ELContext {
    
    private ELResolver resolver;

    public StubELContext() {

    }

    public StubELContext(ELResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public ELResolver getELResolver() {
        return resolver;
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
