package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.account.dao.AccountDao;
import io.zingoworks.demospringbook.message.dao.MessageDao;

public class DaoFactory {
	
	public UserDao userDao() {
		ConnectionMaker connectionMaker = new DConnectionMaker();
		return new UserDao(connectionMaker);
	}
	
	public AccountDao accountDao() {
		ConnectionMaker connectionMaker = new DConnectionMaker();
		return new AccountDao(connectionMaker);
	}
	
	public MessageDao messageDao() {
		ConnectionMaker connectionMaker = new DConnectionMaker();
		return new MessageDao(connectionMaker);
	}
}
