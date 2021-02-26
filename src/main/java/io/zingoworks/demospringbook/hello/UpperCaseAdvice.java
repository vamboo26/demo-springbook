package io.zingoworks.demospringbook.hello;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class UpperCaseAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String returnValue = (String) methodInvocation.proceed();
        return returnValue.toUpperCase();
    }
}
