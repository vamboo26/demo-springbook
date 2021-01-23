package io.zingoworks.demospringbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

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
}
