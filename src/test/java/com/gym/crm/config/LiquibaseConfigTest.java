package com.gym.crm.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(TestAppConfig.class)
class LiquibaseConfigTest {

    private static final String USERS_TABLE = "users";
    private static final String TRAINEES_TABLE = "trainees";
    private static final String TRAINERS_TABLE = "trainers";
    private static final String TRAININGS_TABLE = "trainings";
    private static final String TRAINING_TYPES_TABLE = "training_types";
    private static final String TRAINEES_TRAINERS_TABLE = "trainees_trainers";

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldCreateRequiredTables() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(tableExists(metaData, USERS_TABLE));
            assertTrue(tableExists(metaData, TRAINEES_TABLE));
            assertTrue(tableExists(metaData, TRAINERS_TABLE));
            assertTrue(tableExists(metaData, TRAININGS_TABLE));
            assertTrue(tableExists(metaData, TRAINING_TYPES_TABLE));
            assertTrue(tableExists(metaData, TRAINEES_TRAINERS_TABLE));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInUsersTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, USERS_TABLE, "id"));
            assertTrue(columnExists(metaData, USERS_TABLE, "first_name"));
            assertTrue(columnExists(metaData, USERS_TABLE, "last_name"));
            assertTrue(columnExists(metaData, USERS_TABLE, "username"));
            assertTrue(columnExists(metaData, USERS_TABLE, "password"));
            assertTrue(columnExists(metaData, USERS_TABLE, "is_active"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTraineesTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, TRAINEES_TABLE, "id"));
            assertTrue(columnExists(metaData, TRAINEES_TABLE, "date_of_birth"));
            assertTrue(columnExists(metaData, TRAINEES_TABLE, "address"));
            assertTrue(columnExists(metaData, TRAINEES_TABLE, "user_id"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTrainersTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, TRAINERS_TABLE, "id"));
            assertTrue(columnExists(metaData, TRAINERS_TABLE, "specialization_id"));
            assertTrue(columnExists(metaData, TRAINERS_TABLE, "user_id"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTraineesTrainersTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, TRAINEES_TRAINERS_TABLE, "trainee_id"));
            assertTrue(columnExists(metaData, TRAINEES_TRAINERS_TABLE, "trainer_id"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTrainingTypesTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, TRAINING_TYPES_TABLE, "id"));
            assertTrue(columnExists(metaData, TRAINING_TYPES_TABLE, "training_type_name"));
        }
    }

    @Test
    void shouldHaveCorrectColumnsInTrainingsTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            assertTrue(columnExists(metaData, TRAININGS_TABLE, "id"));
            assertTrue(columnExists(metaData, TRAININGS_TABLE, "trainee_id"));
            assertTrue(columnExists(metaData, TRAININGS_TABLE, "trainer_id"));
            assertTrue(columnExists(metaData, TRAININGS_TABLE, "training_name"));
            assertTrue(columnExists(metaData, TRAININGS_TABLE, "training_type_id"));
            assertTrue(columnExists(metaData, TRAININGS_TABLE, "training_date"));
            assertTrue(columnExists(metaData, TRAININGS_TABLE, "training_duration"));
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
