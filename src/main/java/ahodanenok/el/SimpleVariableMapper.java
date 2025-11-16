package ahodanenok.el;

import java.util.HashMap;
import java.util.Map;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;

public class SimpleVariableMapper extends VariableMapper {

    private final Map<String, ValueExpression> variables = new HashMap<>();

    @Override
    public ValueExpression resolveVariable(String variable) {
        return variables.get(variable);
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        ValueExpression prev;
        if (expression == null) {
            prev = variables.remove(variable);
        } else {
            prev = variables.put(variable, expression);
        }

        return prev;
    }
}
