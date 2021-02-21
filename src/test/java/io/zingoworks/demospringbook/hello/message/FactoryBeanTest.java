package io.zingoworks.demospringbook.hello.message;

import static org.assertj.core.api.Assertions.assertThat;

import io.zingoworks.demospringbook.DemoSpringbookApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = DemoSpringbookApplication.class)
class FactoryBeanTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        assertThat(message).isInstanceOf(Message.class);
        assertThat(((Message) message).getText()).isEqualTo("Factory Bean");
    }

    @Test
    void getFactoryBean() {
        Object factory = context.getBean("&message");
        assertThat(factory).isInstanceOf(FactoryBean.class);
        assertThat(factory).isInstanceOf(MessageFactoryBean.class);
    }
}
