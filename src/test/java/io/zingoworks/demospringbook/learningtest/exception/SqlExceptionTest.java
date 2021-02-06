package io.zingoworks.demospringbook.learningtest.exception;

import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SqlExceptionTest {
	
	@Autowired
	private UserDao dao;
	
	@Autowired
	private DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	@BeforeEach
	void setUp() {
		this.user1 = new User("1", "one", "1234");
		this.user2 = new User("2", "two", "1234");
		this.user3 = new User("3", "three", "1234");
	}
	
	@Test
	void translation() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		} catch (DuplicateKeyException ex) {
			SQLException sqlException = (SQLException) ex.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			
			assertThat(set.translate(null, null, sqlException)).isInstanceOf(DuplicateKeyException.class);
		}
		
	}
}
