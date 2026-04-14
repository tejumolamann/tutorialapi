package com.tutorialapi.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.*;
import org.sqlite.JDBC;

import javax.sql.DataSource;

public class DataSourceExtension implements BeforeAllCallback, BeforeEachCallback, ParameterResolver {
    private final DataSource dataSource;

    public DataSourceExtension() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(JDBC.class.getName());
        hikariConfig.setJdbcUrl("jdbc:sqlite::memory:");
        hikariConfig.setUsername("user");
        hikariConfig.setPassword("pass");
        hikariConfig.setAutoCommit(false);

        dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration/todo")
                .load()
                .migrate();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        ServiceFactory serviceFactory = new DefaultServiceFactory(dataSource);
        serviceFactory.getTodoItemService().truncate();
        serviceFactory.getTodoListService().truncate();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(DataSource.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return dataSource;
    }
}
