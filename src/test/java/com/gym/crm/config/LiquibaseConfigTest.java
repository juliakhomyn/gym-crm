package com.gym.crm.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestAppConfig.class, DataSourceConfig.class, LiquibaseConfig.class, HibernateConfig.class})
class LiquibaseConfigTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldCreateRequiredTables() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(tableExists(metaData, "users"));
            assertTrue(tableExists(metaData, "trainees"));
            assertTrue(tableExists(metaData, "trainers"));
            assertTrue(tableExists(metaData, "trainings"));
            assertTrue(tableExists(metaData, "training_types"));
            assertTrue(tableExists(metaData, "trainees_trainers"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInUsersTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, "users", "id"));
            assertTrue(columnExists(metaData, "users", "first_name"));
            assertTrue(columnExists(metaData, "users", "last_name"));
            assertTrue(columnExists(metaData, "users", "username"));
            assertTrue(columnExists(metaData, "users", "password"));
            assertTrue(columnExists(metaData, "users", "is_active"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTraineesTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, "trainees", "id"));
            assertTrue(columnExists(metaData, "trainees", "date_of_birth"));
            assertTrue(columnExists(metaData, "trainees", "address"));
            assertTrue(columnExists(metaData, "trainees", "user_id"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTrainersTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, "trainers", "id"));
            assertTrue(columnExists(metaData, "trainers", "specialization"));
            assertTrue(columnExists(metaData, "trainers", "user_id"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTraineesTrainersTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, "trainees_trainers", "trainee_id"));
            assertTrue(columnExists(metaData, "trainees_trainers", "trainer_id"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTrainingTypesTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, "training_types", "id"));
            assertTrue(columnExists(metaData, "training_types", "training_type_name"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTrainingsTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, "trainings", "id"));
            assertTrue(columnExists(metaData, "trainings", "trainee_id"));
            assertTrue(columnExists(metaData, "trainings", "trainer_id"));
            assertTrue(columnExists(metaData, "trainings", "training_name"));
            assertTrue(columnExists(metaData, "trainings", "training_type_id"));
            assertTrue(columnExists(metaData, "trainings", "training_date"));
            assertTrue(columnExists(metaData, "trainings", "training_duration"));
        }
    }

    private boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), null)) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = metaData.getTables(null, null, tableName.toLowerCase(), null)) {
            return rs.next();
        }
    }

    private boolean columnExists(DatabaseMetaData metaData, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = metaData.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = metaData.getColumns(null, null, tableName.toLowerCase(), columnName.toLowerCase())) {
            return rs.next();
        }
    }
}
