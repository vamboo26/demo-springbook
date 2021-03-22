package io.zingoworks.demospringbook.sql;

public interface SqlService {

    String getSql(String key) throws SqlRetrievalFailureException;
}
