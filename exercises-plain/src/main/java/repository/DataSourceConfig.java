package repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// tag::code[]
@Configuration
public class DataSourceConfig {

    @Bean
    HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:hsqldb:hsql://localhost/xdb");
        config.setUsername("SA");
        config.setPassword("");

        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }
}
// end::code[]
