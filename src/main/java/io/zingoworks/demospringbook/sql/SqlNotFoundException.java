package io.zingoworks.demospringbook.sql;

public class SqlNotFoundException extends RuntimeException {
	
	public SqlNotFoundException(String message) {
		super(message);
	}
}
