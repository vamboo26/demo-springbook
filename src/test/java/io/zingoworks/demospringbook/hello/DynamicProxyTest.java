package io.zingoworks.demospringbook.hello;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.cglib.proxy.Enhancer;

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
    
        System.out.println("proxiedHello.getClass() = " + proxiedHello.getClass());

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
    
        System.out.println("proxiedHello.getClass() = " + proxiedHello.getClass());
    
    
        assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
        assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
        assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("THANK YOU BOB");
    }
    
    @Test
    void cglib() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloWithoutInterface());
    
        HelloWithoutInterface proxied = (HelloWithoutInterface) pfBean.getObject();
    
        System.out.println("proxied.getClass() = " + proxied.getClass());
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

    @Test
    void classNamePointcutAdvisor() {
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return aClass -> aClass.getSimpleName().startsWith("HelloT");
            }
        };

        classMethodPointcut.setMappedName("sayH*");

        checkAdvice(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget {};
        checkAdvice(new HelloWorld(), classMethodPointcut, false);

        class HelloTom extends HelloTarget {};
        checkAdvice(new HelloTom(), classMethodPointcut, true);
    }

    private void checkAdvice(Object target, Pointcut pointcut, boolean advice) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperCaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if(advice) {
            assertThat(proxiedHello.sayHello("Bob")).isEqualTo("HELLO BOB");
            assertThat(proxiedHello.sayHi("Bob")).isEqualTo("HI BOB");
            assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("Thank You Bob");
        } else {
            assertThat(proxiedHello.sayHello("Bob")).isEqualTo("Hello Bob");
            assertThat(proxiedHello.sayHi("Bob")).isEqualTo("Hi Bob");
            assertThat(proxiedHello.sayThankYou("Bob")).isEqualTo("Thank You Bob");
        }
    }
}
