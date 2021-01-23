package io.zingoworks.demospringbook.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//@Component 코드를 이용한 수동 DI (not Spring)
public class JdbcContext {
	
	//	@Autowired 코드를 이용한 수동 DI (not Spring)
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void executeSql(final String query) throws SQLException {
		this.workWithStatementStrategy(
				new StatementStrategy() {
					@Override
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
						return c.prepareStatement(query);
					}
				}
		);
	}
	
	public void executeSqlAdd(String query, String... args) throws SQLException {
		this.workWithStatementStrategy(
				new StatementStrategy() {
					@Override
					public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
						PreparedStatement ps = c.prepareStatement(query);
						
						int index = 1;
						for (String arg : args) {
							ps.setString(index, arg);
							++index;
						}
						return ps;
					}
				}
		);
	}
	
	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = dataSource.getConnection();
			ps = stmt.makePreparedStatement(c);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
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
}
