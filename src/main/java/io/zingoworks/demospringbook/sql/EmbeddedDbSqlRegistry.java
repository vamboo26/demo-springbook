package io.zingoworks.demospringbook.sql;

import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {

    private SimpleJdbcTemplate jdbc;
    private TransactionTemplate transactionTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbc = new SimpleJdbcTemplate(dataSource);
        this.transactionTemplate = new TransactionTemplate(
            new DataSourceTransactionManager(dataSource)
        );
    }

    @Override
    public void registerSql(String key, String value) {
        jdbc.update("insert into sqlmap(key_, sql_) values (?,?)", key, value);
    }

    @Override
    public String findSql(String key) {
        try {
            return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
        } catch (EmptyResultDataAccessException e) {
            throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다.", e);
        }
    }

    @Override
    public void updateSql(String key, String value) {
        int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?", value, key);

        if (affected == 0) {
            throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        }
    }

    @Override
    public void updateSql(final Map<String, String> sqlmap) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (Entry<String, String> entry : sqlmap.entrySet()) {
                    updateSql(entry.getKey(), entry.getValue());
                }
            }
        });
    }
}
