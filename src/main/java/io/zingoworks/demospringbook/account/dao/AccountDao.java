package io.zingoworks.demospringbook.account.dao;

import io.zingoworks.demospringbook.user.dao.ConnectionMaker;

public class AccountDao {
	
	private ConnectionMaker connectionMaker;
	
	public AccountDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
}
