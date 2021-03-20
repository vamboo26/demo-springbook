package io.zingoworks.demospringbook.sql;

public class SqlUpdateFailureException extends RuntimeException {
	
	public SqlUpdateFailureException(String message) {
		super(message);
	}
}
