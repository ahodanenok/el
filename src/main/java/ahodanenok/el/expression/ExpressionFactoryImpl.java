package ahodanenok.el.expression;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import ahodanenok.el.token.Tokenizer;
import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;

public class ExpressionFactoryImpl extends ExpressionFactory {

    @Override
    public <T> T coerceToType(Object obj, Class<T> targetType) {
        Objects.requireNonNull(targetType);
        try {
            return (T) coerceToTypeInternal(obj, targetType);
        } catch (ELException e) {
            throw e;
        } catch (Exception e) {
            if (obj != null) {
                throw new ELException("Failed to convert object of type '%s' to '%s'"
                    .formatted(obj.getClass().getName(), targetType.getName()));
            } else {
                throw new ELException("Failed to covert null to '%s'".formatted(targetType.getName()));
            }
        }
    }

    // If X is of a primitive type, Let X’ be the equivalent "boxed form" of X
    // Otherwise, Let X’ be the same as X
    private Object coerceToTypeInternal(Object obj, Class<?> targetType) {
        // If X is null and Y is not a primitive type and also not a String, return null
        if (obj == null
                && targetType != String.class
                && !isPrimitiveType(targetType)) {
            return null;
        }

        // If Y is of a primitive type, Let Y’ be the equivalent "boxed form" of Y
        // Otherwise, Let Y’ be the same as Y
        Class<?> toType = boxClass(targetType);
        // Apply the rules in Sections Section 1.25.2, “Coerce A to String” to Section 1.25.9, “Coerce A to Any Other Type T” for coercing X’ to Y’
        Object result;
        // 1.25.2. Coerce A to String
        if (toType == String.class) {
            if (obj == null) {
                result = "";
            } else if (obj instanceof String) {
                result = obj;
            } else if (obj instanceof Enum e) {
                result = e.name();
            } else {
                result = obj.toString();
            }
        }
        // 1.25.3. Coerce A to Number type N
        else if (Number.class.isAssignableFrom(toType)) {
            if (obj == null && !isPrimitiveType(targetType)) {
                result = null;
            } else if (obj == null || obj.equals("")) {
                result = 0; // todo: correct number type
            } else if (obj instanceof Character ch) {
                result = Short.valueOf((short) ch.charValue());
            } else if (obj instanceof Boolean) {
                throw new ELException("Can't convert a boolean value to '%s'".formatted(toType.getName()));
            } else if (toType.isInstance(obj)) {
                result = obj;
            } else if (obj instanceof Number n) {
                if (toType == BigDecimal.class) {
                    if (n instanceof BigInteger bi) {
                        result = new BigDecimal(bi);
                    } else {
                        result = new BigDecimal(n.doubleValue());
                    }
                } else if (toType == BigInteger.class) {
                    if (n instanceof BigDecimal bd) {
                        result = bd.toBigInteger();
                    } else {
                        result = BigInteger.valueOf(n.longValue());
                    }
                } else if (toType == Double.class) {
                    result = Double.valueOf(n.doubleValue());
                } else if (toType == Float.class) {
                    result = Float.valueOf(n.floatValue());
                } else if (toType == Long.class) {
                    result = Long.valueOf(n.longValue());
                } else if (toType == Integer.class) {
                    result = Integer.valueOf(n.intValue());
                } else if (toType == Short.class) {
                    result = Short.valueOf(n.shortValue());
                } else if (toType == Byte.class) {
                    result = Byte.valueOf(n.byteValue());
                } else {
                    throw new ELException("Can't convert a number of type '%s' to '%s'"
                        .formatted(n.getClass().getName(), toType.getName()));
                }
            } else if (obj instanceof String s) {
                if (toType == BigDecimal.class) {
                    result = new BigDecimal(s);
                } else if (toType == BigInteger.class) {
                    result = new BigInteger(s);
                } else if (toType == Double.class) {
                    result = Double.valueOf(s);
                } else if (toType == Float.class) {
                    result = Float.valueOf(s);
                } else if (toType == Long.class) {
                    result = Long.valueOf(s);
                } else if (toType == Integer.class) {
                    result = Integer.valueOf(s);
                } else if (toType == Short.class) {
                    result = Short.valueOf(s);
                } else if (toType == Byte.class) {
                    result = Byte.valueOf(s);
                } else {
                    throw new ELException("Can't convert a string to '%s'"
                        .formatted(obj.getClass().getName(), toType.getName()));
                }
            } else {
                throw new ELException("Can't convert value of type '%s' to '%s'"
                    .formatted(obj.getClass().getName(), toType.getName()));
            }
        }
        // 1.25.4. Coerce A to Character or char
        else if (toType == Character.class) {
            if (obj == null && targetType != char.class) {
                result = null;
            } else if (obj == null || obj.equals("")) {
                result = '\0';
            } else if (obj instanceof Character ch) {
                result = ch;
            } else if (obj instanceof Boolean) {
                throw new ELException("Can't convert a boolean value to character");
            } else if (obj instanceof Number n) {
                result = (char) n.shortValue();
            } else if (obj instanceof String s) {
                result = s.charAt(0);
            } else {
                throw new ELException("Can't convert value of type '%s' to character"
                    .formatted(obj.getClass().getName()));
            }
        }
        // 1.25.5. Coerce A to Boolean or boolean
        else if (toType == Boolean.class) {
            if (obj == null && targetType != boolean.class) {
                result = null;
            } else if (obj == null || obj.equals("")) {
                result = Boolean.FALSE;
            } else if (obj instanceof Boolean b) {
                result = b;
            } else if (obj instanceof String s) {
                result = Boolean.valueOf(s);
            } else {
                throw new ELException("Can't convert value of type '%s' to boolean"
                    .formatted(obj.getClass().getName()));
            }
        }
        // 1.25.6. Coerce A to an Enum Type T
        else if (Enum.class.isAssignableFrom(toType)) {
            if (obj == null) {
                result = null;
            } else if (toType.isAssignableFrom(obj.getClass())) {
                result = obj;
            } else if (obj.equals("")) {
                result = null;
            } else if (obj instanceof String s) {
                result = Enum.valueOf((Class<? extends Enum>) toType, s);
            } else {
                throw new ELException("Can't convert value of type '%s' to enum '%s'"
                    .formatted(obj.getClass().getName(), toType.getName()));
            }
        }
        // 1.25.7. Coerce A to an array of Type T
        else if (toType.isArray()) {
            if (obj == null) {
                result = null;
            } else if (toType.isAssignableFrom(obj.getClass())) {
                result = toType.cast(obj);
            } else if (obj.getClass().isArray()) {
                int length = Array.getLength(obj);
                Object array = Array.newInstance(toType.getComponentType(), length);
                for (int i = 0; i < length; i++) {
                    Array.set(array, i, coerceToTypeInternal(Array.get(obj, i), toType));
                }

                result = array;
            } else {
                throw new ELException("Can't convert value of type '%s' to array '%s"
                    .formatted(obj.getClass().getName(), toType.getName()));
            }
        }
        // 1.25.8. Coerce A to functional interface method invocation
        // todo: impl

        // 1.25.9. Coerce A to Any Other Type T
        else  {
            if (obj == null) {
                result = null;
            } else if (toType.isAssignableFrom(obj.getClass())) {
                result = obj;
            } else if (obj instanceof String s) {
                PropertyEditor editor = PropertyEditorManager.findEditor(toType);
                if (editor != null) {
                    try {
                        editor.setAsText(s);
                        result = editor.getValue();
                    } catch (Exception e) {
                        if (s.equals("")) {
                            result = null;
                        } else {
                            throw new ELException("Can't convert a string to '%s"
                                .formatted(obj.getClass().getName(), toType.getName()), e);
                        }
                    }
                } else if (s.equals("")) {
                    result = null;
                } else {
                    throw new ELException("Can't convert value of type '%s' to '%s"
                        .formatted(obj.getClass().getName(), toType.getName()));
                }
            } else {
                throw new ELException("Can't convert value of type '%s' to '%s"
                    .formatted(obj.getClass().getName(), toType.getName()));
            }
        }

        // If Y is a primitive type, then the result is found by "unboxing" the result of the coercion.
        // If the result of the coercion is null, then error
        if (isPrimitiveType(targetType)) {
            // todo: does it make sense to do the unboxing if a primitive value can't be returned?
            // result = unboxObj(result);
            if (result == null) {
                throw new ELException("Can't convert null to a primitive type '%s'".formatted(targetType.getSimpleName()));
            }
        }
        // If Y is not a primitive type, then the result is the result of the coercion

        return result;
    }

    private boolean isPrimitiveType(Class<?> clazz) {
        return clazz == boolean.class
            || clazz == char.class
            || clazz == byte.class
            || clazz == short.class
            || clazz == int.class
            || clazz == long.class
            || clazz == float.class
            || clazz == double.class;
    }

    private Class<?> boxClass(Class<?> clazz) {
        if (clazz == boolean.class) {
            return Boolean.class;
        } else if (clazz == char.class) {
            return Character.class;
        } else if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == short.class) {
            return Short.class;
        } else if (clazz == int.class) {
            return Integer.class;
        } else if (clazz == long.class) {
            return Long.class;
        } else if (clazz == float.class) {
            return Float.class;
        } else if (clazz == double.class) {
            return Double.class;
        } else {
            return clazz;
        }
    }

    @Override
    public MethodExpression createMethodExpression(ELContext context, String arg1, Class<?> arg2, Class<?>[] arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
        StaticValueExpression expr = new StaticValueExpression(instance);
        expr.expectedType = expectedType;
        return expr;
    }

    @Override
    public ValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(expression));
        Parser parser = new Parser(tokenizer, context);

        ValueExpressionBase expr = parser.parseValue();
        expr.expressionString = expression;
        expr.expectedType = expectedType;

        return expr;
    }
}
