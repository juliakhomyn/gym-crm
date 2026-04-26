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
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM training_types")) {
            rs.next();

            assertEquals(3, rs.getInt(1));
        }
    }

    @Test
    void shouldInsertExpectedTrainingTypeNames() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT training_type_name FROM training_types ORDER BY id")) {
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("training_type_name"));
            }

            assertThat(names).containsExactly("Yoga", "Pilates", "Cardio");
        }
    }

    @Test
    void shouldInsertThreeUsers() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            rs.next();

            assertEquals(3, rs.getInt(1));
        }
    }

    @Test
    void shouldInsertUsersWithCorrectUsernames() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT username FROM users ORDER BY id")) {
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("username"));
            }

            assertThat(names).containsExactly("Callum.Whitfield", "Nora.Pemberton", "Ellis.Hargrove");
        }
    }

    @Test
    void shouldInsertOneTrainer() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM trainers")) {
            rs.next();

            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    void shouldLinkTrainerToCorrectUser() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT u.username FROM trainers t JOIN users u ON t.user_id = u.id")) {
            rs.next();

            assertEquals("Callum.Whitfield", rs.getString(1));
        }
    }

    @Test
    void shouldInsertTwoTrainees() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM trainees")) {
            rs.next();

            assertEquals(2, rs.getInt(1));
        }
    }

    @Test
    void shouldInsertTwoTrainings() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM trainings")) {
            rs.next();

            assertEquals(2, rs.getInt(1));
        }
    }

    @Test
    void shouldLinkTrainingsToCorrectTraineeAndTrainer() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM trainings t " +
                             "JOIN trainees tne ON t.trainee_id = tne.id " +
                             "JOIN trainers tnr ON t.trainer_id = tnr.id")) {
            rs.next();

            assertEquals(2, rs.getInt(1));
        }
    }

    @Test
    void shouldInsertTwoTraineesTrainersRelationships() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM trainees_trainers")) {
            rs.next();

            assertEquals(2, rs.getInt(1));
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
