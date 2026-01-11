package ahodanenok.el.expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jakarta.el.ELContext;

class MethodExpressionUtils {

    private static final Method findMethodImpl;
    static {
        Method method;
        try {
            // why is this algo is not made visible outside?
            Class<?> utilClass = Class.forName("jakarta.el.ELUtil");
            method = utilClass.getDeclaredMethod(
                "findMethod",
                Class.class,
                Object.class,
                String.class,
                Class[].class,
                Object[].class);
            method.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            method = null;
        }

        findMethodImpl = method;
    }

    static Method findMethod(Object obj, String methodName, Object[] args) {
        try {
            return (Method) findMethodImpl.invoke(
                null,
                obj.getClass(),
                obj,
                methodName,
                null,
                args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
