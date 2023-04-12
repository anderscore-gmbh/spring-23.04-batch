package repository;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

// @formatter:off
// tag::code[]
@SpringJUnitConfig(DataSourceConfig.class)
public class SpringBatchSchemaSetup {

    @Test
    @Sql(scripts = "/org/springframework/batch/core/schema-hsqldb.sql", 
         statements = { "SET DATABASE TRANSACTION CONTROL MVCC",
                        "SET PROPERTY \"sql.enforce_strict_size\" TRUE" })
    void initSchema() {
    }
}
// end::code[]
//@formatter:on
