package io.zingoworks.demospringbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;


@SpringBootApplication
public class DemoSpringbookApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoSpringbookApplication.class, args);
	}
	
	@Bean
	public SimpleDriverDataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/springbook");
		dataSource.setUsername("root");
		dataSource.setPassword("1234");
		return dataSource;
	}
}
