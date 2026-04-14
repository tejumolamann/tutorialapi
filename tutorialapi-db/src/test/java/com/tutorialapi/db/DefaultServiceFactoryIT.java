package com.tutorialapi.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataSourceExtension.class)
class DefaultServiceFactoryIT {
    private final DataSource dataSource;

    DefaultServiceFactoryIT(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Test
    void test() {
        ServiceFactory serviceFactory =  new DefaultServiceFactory(dataSource);
        assertNotNull(serviceFactory.getTodoItemService());
        assertNotNull(serviceFactory.getTodoListService());
    }
}