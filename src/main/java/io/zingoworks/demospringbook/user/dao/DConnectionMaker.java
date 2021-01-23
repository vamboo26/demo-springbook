package io.zingoworks.demospringbook.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@Component
@Qualifier("qualifiedConnectionMaker")
public class DConnectionMaker implements ConnectionMaker {
	
	@Override
	public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
		log.debug("DUserDao vendor");
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(
				"jdbc:mysql://localhost/springbook", "root", "1234");
	}
}
