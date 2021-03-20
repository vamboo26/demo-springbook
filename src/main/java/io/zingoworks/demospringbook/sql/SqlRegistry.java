package io.zingoworks.demospringbook.sql;

public interface SqlRegistry {
	
	void registerSql(String key, String value);
	String findSql(String key);
}
