package ahodanenok.el.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;

class MapValueExpression extends ValueExpressionBase {

    final List<Map.Entry<ValueExpressionBase, ValueExpressionBase>> entries;

    MapValueExpression(List<Map.Entry<ValueExpressionBase, ValueExpressionBase>> entries) {
        this.entries = entries;
    }

    @Override
    public <T> T getValue(ELContext context) {
        Map<Object, Object> map = new HashMap<>();
        for (Map.Entry<ValueExpressionBase, ValueExpressionBase> entry : entries) {
            map.put(entry.getKey().getValue(context), entry.getValue().getValue(context));
        }

        return convertIfNecessary(context, map);
    }

    @Override
    public int hashCode() {
        return entries.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != MapValueExpression.class) {
            return false;
        }

        MapValueExpression other = (MapValueExpression) obj;
        return entries.equals(other.entries);
    }
}
