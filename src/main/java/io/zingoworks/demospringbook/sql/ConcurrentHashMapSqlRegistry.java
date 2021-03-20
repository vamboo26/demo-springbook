package io.zingoworks.demospringbook.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
	
	private Map<String, String> sqlMap = new ConcurrentHashMap<>();
	
	@Override
	public void registerSql(String key, String value) {
		sqlMap.put(key, value);
	}
	
	@Override
	public String findSql(String key) {
		String sql = sqlMap.get(key);
		if (sql == null) {
			throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		} else {
			return sql;
		}
	}
	
	@Override
	public void updateSql(String key, String value) {
		if (sqlMap.get(key) == null) {
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
		}
		sqlMap.put(key, value);
	}
	
	@Override
	public void updateSql(Map<String, String> sqlmap) {
		for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
			updateSql(entry.getKey(), entry.getValue());
		}
	}
}
