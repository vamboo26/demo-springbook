package io.zingoworks.demospringbook.sql;

import org.springframework.dao.EmptyResultDataAccessException;

public class SqlNotFoundException extends RuntimeException {

    public SqlNotFoundException(String message) {
        super(message);
    }

    public SqlNotFoundException(String message, EmptyResultDataAccessException e) {
        super(message, e);
    }
}
