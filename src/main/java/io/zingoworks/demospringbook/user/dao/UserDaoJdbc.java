package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Setter
@Repository
public class UserDaoJdbc implements UserDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<User> userMapper = (resultSet, i) -> {
		User user = new User();
		user.setId(resultSet.getString("id"));
		user.setName(resultSet.getString("name"));
		user.setPassword(resultSet.getString("password"));
		user.setLevel(Level.findByValue(resultSet.getInt("level")));
		user.setLoginSequence(resultSet.getInt("login"));
		user.setRecommendationCount(resultSet.getInt("recommend"));
		return user;
	};
	
	@Override
	public void add(User user) {
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend) values(?,?,?,?,?,?)", user.getId(), user.getName(), user.getPassword(), user.getLevel().getValue(), user.getLoginSequence(), user.getRecommendationCount());
	}
	
	@Override
	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
				new Object[]{id},
				this.userMapper);
	}
	
	@Override
	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}
	
	@Override
	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}
	
	@Override
	public int getCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
	}
	
	@Override
	public void update(User user) {
		this.jdbcTemplate.update(
				"update users set name = ?, password = ?, level = ?, login = ?, recommend = ? where id = ?",
				user.getName(),
				user.getPassword(),
				user.getLevel().getValue(),
				user.getLoginSequence(),
				user.getRecommendationCount(),
				user.getId()
		);
	}
}
