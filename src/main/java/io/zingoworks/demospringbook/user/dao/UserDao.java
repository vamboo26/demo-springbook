package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.User;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Setter
@Component
public class UserDao {
	
	private JdbcContext jdbcContext;
	
	@Autowired
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	// JdbcContext가 의존하는 다른 Bean은 UserDao가 DI컨테이너의 역할로 주입
	@PostConstruct
	public void setJdbcContext() {
		this.jdbcContext = new JdbcContext();
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
		jdbcContext.setDataSource(this.dataSource);
	}
	
	public void add(User user) {
		this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
				new Object[]{id},
				new RowMapper<User>() {
					@Override
					public User mapRow(ResultSet resultSet, int i) throws SQLException {
						User user = new User();
						user.setId(resultSet.getString("id"));
						user.setName(resultSet.getString("name"));
						user.setPassword(resultSet.getString("password"));
						return user;
					}
				});
	}
	
	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id",
				new RowMapper<User>() {
					@Override
					public User mapRow(ResultSet resultSet, int i) throws SQLException {
						User user = new User();
						user.setId(resultSet.getString("id"));
						user.setName(resultSet.getString("name"));
						user.setPassword(resultSet.getString("password"));
						return user;
					}
				});
	}
	
	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
	}
	
	public int getCount2() {
		return this.jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				return connection.prepareStatement("select count(*) from users");
			}
		}, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				resultSet.next();
				return resultSet.getInt(1);
			}
		});
	}
}
