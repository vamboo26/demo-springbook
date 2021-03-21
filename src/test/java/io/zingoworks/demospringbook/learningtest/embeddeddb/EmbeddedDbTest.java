package io.zingoworks.demospringbook.learningtest.embeddeddb;

import static org.assertj.core.api.Assertions.assertThat;

import io.zingoworks.demospringbook.sql.SimpleJdbcTemplate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class EmbeddedDbTest {

    private EmbeddedDatabase db;
    private SimpleJdbcTemplate template;

    @BeforeEach
    void setUp() {
        db = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema.sql")
            .addScript("classpath:data.sql")
            .build();

        template = new SimpleJdbcTemplate(db);
    }

    @AfterEach
    void tearDown() {
        db.shutdown();
    }

    @Test
    void initData() {
        assertThat(template.queryForInt("select count(*) from sqlmap")).isEqualTo(3);

        List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");

        assertThat(list.get(0).get("key_")).isEqualTo("KEY1");
        assertThat(list.get(0).get("sql_")).isEqualTo("SQL1");
        assertThat(list.get(1).get("key_")).isEqualTo("KEY2");
        assertThat(list.get(1).get("sql_")).isEqualTo("SQL2");
    }

    @Test
    void insert() {
        template.update("insert into sqlmap(key_, sql_) values (?, ?)", "KEY4", "SQL4");

        assertThat(template.queryForInt("select count(*) from sqlmap")).isEqualTo(4);
    }
}
