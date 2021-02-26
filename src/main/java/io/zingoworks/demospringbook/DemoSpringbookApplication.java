package io.zingoworks.demospringbook;

import io.zingoworks.demospringbook.core.CoreService;
import io.zingoworks.demospringbook.hello.message.MessageFactoryBean;
import io.zingoworks.demospringbook.user.service.TransactionAdvice;
import io.zingoworks.demospringbook.user.service.TxProxyFactoryBean;
import io.zingoworks.demospringbook.user.service.UserServiceImpl;
import javax.sql.DataSource;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class DemoSpringbookApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringbookApplication.class, args);
    }

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://localhost/springbook");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("1234");
        return dataSourceBuilder.build();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean
    public MessageFactoryBean message() {
        MessageFactoryBean messageFactoryBean = new MessageFactoryBean();
        messageFactoryBean.setText("Factory Bean");
        return messageFactoryBean;
    }

    @Bean
    public ProxyFactoryBean userService() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new UserServiceImpl());
        pfBean.setInterceptorNames("transactionAdvisor");
        return pfBean;
    }

    @Bean
    public TxProxyFactoryBean coreService() {
        TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
        txProxyFactoryBean.setTransactionManager(this.platformTransactionManager());
        txProxyFactoryBean.setPattern("");
        txProxyFactoryBean.setServiceInterface(CoreService.class);
        return txProxyFactoryBean;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");

        TransactionAdvice advice = new TransactionAdvice();
        advice.setTransactionManager(platformTransactionManager());

        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
