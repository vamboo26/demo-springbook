package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.account.dao.AccountDao;
import io.zingoworks.demospringbook.message.dao.MessageDao;

public class DaoFactory {
	
	public UserDao userDao() {
		ConnectionMaker connectionMaker = connectionMaker();
		return new UserDao(connectionMaker);
	}
	
	public AccountDao accountDao() {
		ConnectionMaker connectionMaker = connectionMaker();
		return new AccountDao(connectionMaker);
	}
	
	public MessageDao messageDao() {
		ConnectionMaker connectionMaker = connectionMaker();
		return new MessageDao(connectionMaker);
	}
	
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
}
