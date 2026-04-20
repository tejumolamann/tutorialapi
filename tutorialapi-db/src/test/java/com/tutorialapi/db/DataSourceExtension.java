package com.tutorialapi.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.extension.*;
import org.sqlite.JDBC;

import javax.sql.DataSource;

public class DataSourceExtension implements BeforeEachCallback, ParameterResolver {
    private final DataSource dataSource;
    private final ServiceFactory serviceFactory;

    public DataSourceExtension() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(JDBC.class.getName());
        hikariConfig.setJdbcUrl("jdbc:sqlite::memory:");
        hikariConfig.setUsername("user");
        hikariConfig.setPassword("pass");
        hikariConfig.setAutoCommit(false);

        dataSource = new HikariDataSource(hikariConfig);
        serviceFactory = new DefaultServiceFactory(dataSource);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
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
