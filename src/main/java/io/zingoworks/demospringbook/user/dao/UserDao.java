package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.User;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Setter
@Component
public class UserDao {
	
	// @Autowired 코드를 이용한 수동 DI (not Spring)
	private JdbcContext jdbcContext;
	
	@Autowired
	private DataSource dataSource;
	
	// JdbcContext가 의존하는 다른 Bean은 UserDao가 DI컨테이너의 역할로 주입
	@PostConstruct
	public void setJdbcContext() {
		this.jdbcContext = new JdbcContext();
		jdbcContext.setDataSource(this.dataSource);
	}
	
	public void add(User user) throws SQLException {
		this.jdbcContext.workWithStatementStrategy(c -> {
			PreparedStatement ps = c.prepareStatement(
					"insert into users(id, name, password) values(?,?,?)");
			
			ps.setString(1, user.getId());
			ps.setString(2, user.getName());
			ps.setString(3, user.getPassword());
			return ps;
		});
	}
	
	public User get(String id) throws SQLException {
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement(
				"select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		User user = null;
		
		if (rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		
		rs.close();
		ps.close();
		c.close();
		
		if (user == null) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return user;
	}
	
	public void deleteAll() throws SQLException {
		executeSql("delete from users"); // 변하는 쿼리와 변하지 않는 수행과정을 분리
	}
	
	private void executeSql(final String query) throws SQLException {
		this.jdbcContext.workWithStatementStrategy(
				new StatementStrategy() {
					@Override
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
						return c.prepareStatement(query);
					}
				}
		);
	}
	
	public void deleteAllLegacy() throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = dataSource.getConnection();
			
			DeleteAllStatement deleteAllStatement = new DeleteAllStatement();
			ps = deleteAllStatement.makePreparedStatement(c); //FIXME 클라이언트로서 strategy 를 주입하는 게 아닌, concrete strategy 를 직접 알고, 사용하는 모습 💩
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ignored) {
				}
			}
			if (c != null) {
				try {
					c.close();
				} catch (SQLException ignored) {
				}
			}
		}
	}
	
	public int getCount() throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement(" select count(*) from users");
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				
				}
			}
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
				
				}
			}
		}
	}

//	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
//		Connection c = null;
//		PreparedStatement ps = null;
//
//		try {
//			c = dataSource.getConnection();
//			ps = stmt.makePreparedStatement(c);
//			ps.executeUpdate();
//		} catch (SQLException e) {
//			throw e;
//		} finally {
//			if (ps != null) {
//				try {
//					ps.close();
//				} catch (SQLException e) {
//
//				}
//			}
//			if (c != null) {
//				try {
//					c.close();
//				} catch (SQLException e) {
//
//				}
//			}
//		}
//	}
}
