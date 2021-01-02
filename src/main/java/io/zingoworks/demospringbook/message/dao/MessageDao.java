package io.zingoworks.demospringbook.message.dao;

import io.zingoworks.demospringbook.user.dao.ConnectionMaker;

public class MessageDao {
	
	private ConnectionMaker connectionMaker;
	
	public MessageDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
}
