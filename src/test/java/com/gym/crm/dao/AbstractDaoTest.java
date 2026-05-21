package com.gym.crm.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.gym.crm.config.TestAppConfig;
import com.gym.crm.config.TestDataSourceConfig;
import com.gym.crm.config.TestDbUnitConfig;
import com.gym.crm.config.TestLiquibaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
@SpringJUnitConfig(classes = {TestAppConfig.class, TestDbUnitConfig.class, TestDataSourceConfig.class, TestLiquibaseConfig.class})
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
public abstract class AbstractDaoTest<T> {

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected T dao;
}
