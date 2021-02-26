package io.zingoworks.demospringbook.hello;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

class DynamicProxyTest {

    @Test
    void simpleProxy_client() {
        Hello hello = new HelloTarget();

        assertThat(hello.sayHello("Bob")).isEqualTo("Hello Bob");
        assertThat(hello.sayHi("Bob")).isEqualTo("Hi Bob");
        assertThat(hello.sayThankYou("Bob")).isEqualTo("Thank You Bob");
    }

    @Test
    void simpleProxy_decorate() {
        Hello proxiedHello = new HelloUppercase(new HelloTarget());

        assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
        assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
        assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("THANK YOU BOB");
    }

    @Test
    void dynamicProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[]{Hello.class},
            new UppercaseHandler(new HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
        assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
        assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("THANK YOU BOB");
    }

    @Test
    void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UpperCaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
        assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
        assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("THANK YOU BOB");
    }

    @Test
    void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperCaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
        assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
        assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("Thank You Bob");
    }
}
