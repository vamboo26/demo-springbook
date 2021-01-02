package io.zingoworks.demospringbook.user.dao;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DUserDao extends UserDao {
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		log.debug("DUserDao vendor");
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(
				"jdbc:mysql://localhost/springbook", "root", "1234");
	}
}
