package io.zingoworks.demospringbook.sql;

import org.springframework.core.io.InputStreamSource;

public interface Resource extends InputStreamSource {

    boolean exists();
    boolean isReadable();
    boolean isOpen();
}
