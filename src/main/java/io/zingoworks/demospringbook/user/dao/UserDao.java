package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
	
	private ConnectionMaker connectionMaker;
	
//	public UserDao(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker;
//	}
	
	// setter를 통한 의존성 주입
	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
//	public UserDao() {
//		// DaoFactory를 통한 의존관계 검색
//		CountingDaoFactory countingDaoFactory = new CountingDaoFactory();
//		this.connectionMaker = countingDaoFactory.connectionMaker();
//	}
	
//	public UserDao() {
//		// 스프링의 IoC 컨테이너(애플리케이션 컨텍스트)를 통한 의존관계 검색
//		AnnotationConfigApplicationContext applicationContext =
//				new AnnotationConfigApplicationContext(DaoFactory.class);
//		this.connectionMaker = applicationContext.getBean("connectionMaker", ConnectionMaker.class);
//	}
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		Connection c = this.connectionMaker.makeNewConnection();
		
		PreparedStatement ps = c.prepareStatement(
				"insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = this.connectionMaker.makeNewConnection();
		
		PreparedStatement ps = c.prepareStatement(
				"select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
}
