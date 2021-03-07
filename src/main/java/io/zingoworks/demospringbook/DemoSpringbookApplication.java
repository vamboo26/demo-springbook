package io.zingoworks.demospringbook;

import io.zingoworks.demospringbook.core.CoreService;
import io.zingoworks.demospringbook.hello.message.MessageFactoryBean;
import io.zingoworks.demospringbook.user.service.TransactionAdvice;
import io.zingoworks.demospringbook.user.service.TxProxyFactoryBean;
import javax.sql.DataSource;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
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
    public TxProxyFactoryBean coreService() {
        TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
        txProxyFactoryBean.setTransactionManager(this.platformTransactionManager());
        txProxyFactoryBean.setPattern("");
        txProxyFactoryBean.setServiceInterface(CoreService.class);
        return txProxyFactoryBean;
    }

//    @Bean
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        return new DefaultAdvisorAutoProxyCreator();
//    }
    
    @Bean
    public DefaultAdvisorAutoProxyCreator myDefaultAdvisorAutoProxyCreator() {
        return new MyDefaultAdvisorAutoProxyCreator();
    }
    
    @Bean
    public AspectJExpressionPointcut transactionPointcut() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("bean(*Service)");
        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        TransactionAdvice advice = new TransactionAdvice();
        advice.setTransactionManager(platformTransactionManager());

        return new DefaultPointcutAdvisor(transactionPointcut(), advice);
    }
}
