package io.zingoworks.demospringbook.hello;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ExtendedUppercaseHandler implements InvocationHandler {

    private Object target;

    private ExtendedUppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = method.invoke(target, args);
        if (returnValue instanceof String && method.getName().startsWith("say")) {
            return ((String) returnValue).toUpperCase();
        } else {
            return returnValue;
        }
    }
}
