package ops.quartz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Utility zum Anlegen der Quartz-Tabellen.
 */
@SpringBootTest
public class QuartzSchemaSetup {

    @Test
    @Sql("/org/quartz/impl/jdbcjobstore/tables_hsqldb.sql")
    void initSchema() {
    }
}
