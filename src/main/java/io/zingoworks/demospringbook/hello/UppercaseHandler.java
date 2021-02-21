package io.zingoworks.demospringbook.hello;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

    private Hello target;

    public UppercaseHandler(Hello target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String returnValue = (String) method.invoke(target, args);
        return returnValue.toUpperCase();
    }
}
