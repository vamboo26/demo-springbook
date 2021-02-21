package io.zingoworks.demospringbook.hello;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;

class HelloTest {

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
}
