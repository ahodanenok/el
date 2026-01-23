package ahodanenok.el;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.MethodNotFoundException;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;

public final class StreamELResolver extends ELResolver {

    private static final String FILTER_FN = "filter";
    private static final String MAP_FN = "map";
    private static final String FLAT_MAP_FN = "flatMap";
    private static final String DISTINCT_FN = "distinct";
    private static final String SORTED_FN = "sorted";
    private static final String FOR_EACH_FN = "forEach";
    private static final String PEEK_FN = "peek";
    private static final String ITERATOR_FN = "iterator";
    private static final String LIMIT_FN = "limit";
    private static final String SUBSTREAM_FN = "substream";
    private static final String TO_ARRAY_FN = "toArray";
    private static final String TO_LIST_FN = "toList";
    private static final String REDUCE_FN = "reduce";
    private static final String MAX_FN = "max";
    private static final String MIN_FN = "min";
    private static final String AVERAGE_FN = "average";
    private static final String SUM_FN = "sum";
    private static final String COUNT_FN = "count";
    private static final String ANY_MATCH_FN = "anyMatch";
    private static final String ALL_MATCH_FN = "allMatch";
    private static final String NONE_MATCH_FN = "noneMatch";
    private static final String FIND_FIRST_FN = "findFirst";

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        if (!(base instanceof Stream<?>)) {
            return null;
        }

        Stream<Object> stream = (Stream<Object>) base;
        context.setPropertyResolved(base, method);
        String methodName = method.toString();
        return switch (methodName) {
            case FILTER_FN -> stream.filter(context.convertToType(params[0], Predicate.class));
            case MAP_FN -> stream.map(context.convertToType(params[0], Function.class));
            case FLAT_MAP_FN -> stream.flatMap(context.convertToType(params[0], Function.class));
            case DISTINCT_FN -> stream.distinct();
            case SORTED_FN -> {
                if (params.length == 0) {
                    yield stream.sorted();
                } else if (params.length == 1) {
                    yield stream.sorted(context.convertToType(params[0], Comparator.class));
                } else {
                    throw new MethodNotFoundException(methodName);
                }
            }
            case FOR_EACH_FN -> {
                stream.forEach(context.convertToType(params[0], Consumer.class));
                yield null;
            }
            case PEEK_FN -> stream.peek(context.convertToType(params[0], Consumer.class));
            case ITERATOR_FN -> stream.iterator();
            case LIMIT_FN -> stream.limit(context.convertToType(params[0], long.class));
            case SUBSTREAM_FN -> {
                long start = context.convertToType(params[0], long.class);
                if (start < 0) {
                    start = 0;
                }
                if (params.length == 1) {
                    yield start == 0 ? stream : stream.skip(start);
                } else if (params.length == 2) {
                    long end = context.convertToType(params[1], long.class);
                    yield end <= start
                        ? stream.skip(start).limit(0)
                        : stream.skip(start).limit(end - start);
                } else {
                    throw new MethodNotFoundException(methodName);
                }
            }
            case TO_ARRAY_FN -> stream.toArray();
            case TO_LIST_FN -> stream.toList();
            case REDUCE_FN -> {
                if (params.length == 1) {
                    yield stream.reduce(context.convertToType(params[0], BinaryOperator.class));
                } else if (params.length == 2) {
                    yield stream.reduce(params[0], context.convertToType(params[1], BinaryOperator.class));
                } else {
                    throw new MethodNotFoundException(methodName);
                }
            }
            case MAX_FN -> {
                if (params.length == 0) {
                    yield stream.sorted((Comparator) Comparator.reverseOrder()).findFirst();
                } else if (params.length == 1) {
                    yield stream.sorted(context.convertToType(params[0], Comparator.class).reversed()).findFirst();
                } else {
                    throw new MethodNotFoundException(methodName);
                }
            }
            case MIN_FN -> {
                if (params.length == 0) {
                    yield stream.sorted().findFirst();
                } else if (params.length == 1) {
                    yield stream.sorted(context.convertToType(params[0], Comparator.class)).findFirst();
                } else {
                    throw new MethodNotFoundException(methodName);
                }
            }
            case AVERAGE_FN -> {
                List<Object> values = stream.toList();
                if (!values.isEmpty()) {
                    yield Optional.of(
                        Ops.divide(
                            context,
                            values.stream().reduce(0L, (a, b) -> Ops.add(context, a, b)),
                            values.size()));
                } else {
                    yield Optional.empty();
                }
            }
            case SUM_FN -> stream.reduce(0L, (a, b) -> Ops.add(context, a, b));
            case COUNT_FN -> stream.count();
            case ANY_MATCH_FN -> stream.anyMatch(context.convertToType(params[0], Predicate.class));
            case ALL_MATCH_FN -> stream.allMatch(context.convertToType(params[0], Predicate.class));
            case NONE_MATCH_FN -> stream.noneMatch(context.convertToType(params[0], Predicate.class));
            case FIND_FIRST_FN -> stream.findFirst();
            default -> throw new PropertyNotFoundException(methodName);
        };
    }

    public Object getValue(ELContext context, Object base, Object property) {
        if (base instanceof Stream<?> stream) {
            context.setPropertyResolved(base, property);
            throw new PropertyNotFoundException(property.toString());
        }

        return null;
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base instanceof Stream<?> stream) {
            context.setPropertyResolved(base, property);
            if (!isKnownFunction(property.toString())) {
                throw new PropertyNotFoundException(property.toString());
            }
        }

        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base instanceof Stream<?> stream) {
            context.setPropertyResolved(base, property);
            if (!isKnownFunction(property.toString())) {
                throw new PropertyNotFoundException(property.toString());
            }

            throw new PropertyNotWritableException(property.toString());
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base instanceof Stream<?> stream) {
            context.setPropertyResolved(base, property);
            if (!isKnownFunction(property.toString())) {
                throw new PropertyNotFoundException(property.toString());
            }
        }

        return true;
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }

    private boolean isKnownFunction(String name) {
        return FILTER_FN.equals(name)
            || MAP_FN.equals(name)
            || FLAT_MAP_FN.equals(name)
            || DISTINCT_FN.equals(name)
            || SORTED_FN.equals(name)
            || FOR_EACH_FN.equals(name)
            || PEEK_FN.equals(name)
            || ITERATOR_FN.equals(name)
            || LIMIT_FN.equals(name)
            || SUBSTREAM_FN.equals(name)
            || TO_ARRAY_FN.equals(name)
            || TO_LIST_FN.equals(name)
            || REDUCE_FN.equals(name)
            || MAX_FN.equals(name)
            || MIN_FN.equals(name)
            || AVERAGE_FN.equals(name)
            || SUM_FN.equals(name)
            || COUNT_FN.equals(name)
            || ANY_MATCH_FN.equals(name)
            || ALL_MATCH_FN.equals(name)
            || NONE_MATCH_FN.equals(name)
            || FIND_FIRST_FN.equals(name);
    }
}
