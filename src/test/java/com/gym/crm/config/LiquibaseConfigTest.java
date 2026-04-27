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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestAppConfig.class, DataSourceConfig.class, LiquibaseConfig.class, HibernateConfig.class})
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

    @Test
    void shouldInsertThreeTrainingTypes() throws SQLException {
        assertEquals(3, countRows(TRAINING_TYPES_TABLE));
    }

    @Test
    void shouldInsertExpectedTrainingTypeNames() throws SQLException {
        List<String> actual = findColumnValues(
                "SELECT training_type_name FROM training_types ORDER BY id",
                "training_type_name"
        );

        assertThat(actual).containsExactly("Yoga", "Pilates", "Cardio");
    }

    @Test
    void shouldInsertThreeUsers() throws SQLException {
        assertEquals(3, countRows(USERS_TABLE));
    }

    @Test
    void shouldInsertUsersWithCorrectUsernames() throws SQLException {
        List<String> actual = findColumnValues(
                "SELECT username FROM users ORDER BY id",
                "username"
        );

        assertThat(actual).containsExactly("Callum.Whitfield", "Nora.Pemberton", "Ellis.Hargrove");
    }

    @Test
    void shouldInsertOneTrainer() throws SQLException {
        assertEquals(1, countRows(TRAINERS_TABLE));
    }

    @Test
    void shouldLinkTrainerToCorrectUser() throws SQLException {
        String actual = findOneColumnValue(
                "SELECT u.username FROM trainers t JOIN users u ON t.user_id = u.id",
                1
        );

        assertEquals("Callum.Whitfield", actual);
    }

    @Test
    void shouldInsertTwoTrainees() throws SQLException {
        assertEquals(2, countRows(TRAINEES_TABLE));
    }

    @Test
    void shouldInsertTwoTrainings() throws SQLException {
        assertEquals(2, countRows(TRAININGS_TABLE));
    }

    @Test
    void shouldLinkTrainingsToCorrectTraineeAndTrainer() throws SQLException {
        String actual = findOneColumnValue(
                "SELECT COUNT(*) FROM trainings t " +
                        "JOIN trainees tne ON t.trainee_id = tne.id " +
                        "JOIN trainers tnr ON t.trainer_id = tnr.id",
                1
        );

        assertEquals(2, Integer.parseInt(actual));
    }

    @Test
    void shouldInsertTwoTraineesTrainersRelationships() throws SQLException {
        assertEquals(2, countRows(TRAINEES_TRAINERS_TABLE));
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

    private int countRows(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();

            return rs.getInt(1);
        }
    }

    private List<String> findColumnValues(String sql, String columnName) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            List<String> values = new ArrayList<>();
            while (rs.next()) {
                values.add(rs.getString(columnName));
            }

            return values;
        }
    }

    private String findOneColumnValue(String sql, int columnIndex) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            rs.next();

            return rs.getString(columnIndex);
        }
    }
}
