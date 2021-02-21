package io.zingoworks.demospringbook;

import static org.assertj.core.api.Assertions.assertThat;

import io.zingoworks.demospringbook.core.CoreService;
import io.zingoworks.demospringbook.user.service.TxProxyFactoryBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class DemoSpringbookApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
    }

    @Test
    void reuse() {
        Object object = context.getBean("coreService");
        assertThat(object).isNotNull();
        assertThat(object).isInstanceOf(CoreService.class);
    }

    @Test
    void reuse_factory_bean() {
        Object object = context.getBean(
            "&coreService",
            TxProxyFactoryBean.class
        );
        assertThat(object).isNotNull();
        assertThat(object).isInstanceOf(TxProxyFactoryBean.class);
    }
}
