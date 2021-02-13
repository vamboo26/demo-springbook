package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserService {
	
	public static final int MIN_LOGIN_SEQUENCE_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;
	
	private UserDao userDao;
	private UserLevelUpgradePolicy userLevelUpgradePolicy;
	private DataSource dataSource;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Autowired
	@Qualifier(value = "defaultUserLevelUpgradePolicy")
	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void upgradeLevels() throws SQLException {
		TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 작업 초기화
		Connection c = DataSourceUtils.getConnection(dataSource); // 커넥션 생성
		c.setAutoCommit(false); // 트랜잭션 시작
		
		try {
			List<User> users = userDao.getAll();
			
			for (User user : users) {
				if (canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			c.commit(); // 정상흐름 : 트랜잭션 커밋
		} catch (Exception e) {
			c.rollback(); // 예외흐름 : 트랙잭션 롤백
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(c, dataSource); // 커넥션 close
			TransactionSynchronizationManager.unbindResource(this.dataSource); // 트랜잭션 동기화 작업 종료
			TransactionSynchronizationManager.clearSynchronization();
		}
	}
	
	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
	}
	
	private boolean canUpgradeLevel(User user) {
		return userLevelUpgradePolicy.canUpgradeLevel(user);
	}
	
	public void add(User user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
}
