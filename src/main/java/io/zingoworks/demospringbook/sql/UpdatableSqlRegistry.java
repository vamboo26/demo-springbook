package io.zingoworks.demospringbook.sql;

import java.util.Map;

public interface UpdatableSqlRegistry extends SqlRegistry {
	void updateSql(String key, String value);
	void updateSql(Map<String, String> sqlmap);
}
