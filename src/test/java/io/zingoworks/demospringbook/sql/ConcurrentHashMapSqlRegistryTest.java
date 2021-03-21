package io.zingoworks.demospringbook.sql;

import org.junit.jupiter.api.BeforeEach;

class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    @BeforeEach
    void setUp() {
        super.sqlRegistry = this.createUpdatableSqlRegistry();
        super.sqlRegistry.registerSql("KEY1", "SQL1");
        super.sqlRegistry.registerSql("KEY2", "SQL2");
        super.sqlRegistry.registerSql("KEY3", "SQL3");
    }

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}
