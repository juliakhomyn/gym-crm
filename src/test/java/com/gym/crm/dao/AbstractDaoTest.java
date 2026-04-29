package com.gym.crm.dao;

import com.gym.crm.config.TestAppConfig;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.Connection;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(classes = {TestAppConfig.class})
public abstract class AbstractDaoTest<T> {
    private static final String DATASET = "dataset/dataset.xml";

    @Autowired
    protected DataSource dataSource;
    @Autowired
    protected ApplicationContext context;

    protected T dao;
    protected IDatabaseConnection dbUnitConnection;

    protected abstract Class<T> getDaoClass();

    @BeforeAll
    void initDbUnit() throws Exception {
        dao = context.getBean(getDaoClass());

        Connection connection = dataSource.getConnection();
        dbUnitConnection = new DatabaseConnection(connection, "PUBLIC");

        DatabaseConfig config = dbUnitConnection.getConfig();
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
        config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, false);
    }

    @BeforeEach
    void loadDataset() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(new ClassPathResource(DATASET).getInputStream());

        DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dataSet);
        DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);
    }
}
