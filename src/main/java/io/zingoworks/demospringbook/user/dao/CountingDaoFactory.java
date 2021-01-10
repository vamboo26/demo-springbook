package io.zingoworks.demospringbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

//@Configuration
public class CountingDaoFactory {
	
	//	@Bean
	public UserDao userDao() {
		return new UserDao();
	}
	
	//	@Bean
	public ConnectionMaker connectionMaker() {
		return new CountingConnectionMaker(realConnectionMaker());
	}
	
	//	@Bean
	public ConnectionMaker realConnectionMaker() {
		return new DConnectionMaker();
	}
	
//	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
//		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
//		dataSource.setUrl("jdbc:mysql://localhost/springbook");
//		dataSource.setUsername("root");
//		dataSource.setPassword("!234");
		
		return dataSource;
	}
}
