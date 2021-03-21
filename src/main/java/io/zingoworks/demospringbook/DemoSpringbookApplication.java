package io.zingoworks.demospringbook;

import io.zingoworks.demospringbook.core.CoreService;
import io.zingoworks.demospringbook.hello.message.MessageFactoryBean;
import io.zingoworks.demospringbook.sql.EmbeddedDbSqlRegistry;
import io.zingoworks.demospringbook.sql.SqlRegistry;
import io.zingoworks.demospringbook.user.service.TransactionAdvice;
import io.zingoworks.demospringbook.user.service.TxProxyFactoryBean;
import javax.sql.DataSource;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class DemoSpringbookApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringbookApplication.class, args);
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/springbook");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        return dataSource;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
//            .addScript("classpath:schema.sql")
//            .addScript("classpath:data.sql")
            .build();
    }

    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(embeddedDatabase());
        return sqlRegistry;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
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
        txProxyFactoryBean.setTransactionManager(this.transactionManager());
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
        advice.setTransactionManager(this.transactionManager());

        return new DefaultPointcutAdvisor(transactionPointcut(), advice);
    }
}
