package com.gym.crm.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvReaderTest {

    private CsvReader csvReader;
    private String validFilePath;
    private String noHeaderFilePath;
    private String emptyFilePath;
    private String withBlankLinesFilePath;

    @BeforeEach
    void setUp() throws URISyntaxException {
        csvReader = new CsvReader();

        validFilePath = Paths.get(
                Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResource("valid.csv")
                ).toURI()
        ).toString();

        noHeaderFilePath = Paths.get(
                Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResource("no_header.csv")
                ).toURI()
        ).toString();

        emptyFilePath = Paths.get(
                Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResource("empty.csv")
                ).toURI()
        ).toString();

        withBlankLinesFilePath = Paths.get(
                Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResource("with_blank_lines.csv")
                ).toURI()
        ).toString();
    }

    @Test
    void readCsv_shouldReadLinesSkippingHeader_whenHeaderPresent() throws IOException {
        List<String> lines = csvReader.readCsv(validFilePath, true);

        assertEquals(2, lines.size());
        assertEquals("1,Callum,Whitfield,test123,true,2004-05-15,123 Main St", lines.get(0));
        assertEquals("2,Nora,Pemberton,test456,false,1999-11-30,456 Oak Ave", lines.get(1));
    }

    @Test
    void readCsv_shouldReadAllLines_whenNoHeader() throws IOException {
        List<String> lines = csvReader.readCsv(noHeaderFilePath, false);

        assertEquals(2, lines.size());
        assertEquals("1,Callum,Whitfield,test123,true,2004-05-15,123 Main St", lines.get(0));
    }

    @Test
    void readCsv_shouldReturnEmptyList_whenFileEmpty() throws IOException {
        List<String> lines = csvReader.readCsv(emptyFilePath, false);

        assertTrue(lines.isEmpty());
    }

    @Test
    void readCsv_shouldThrow_whenFileNameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> csvReader.readCsv(null, false));

        assertEquals("File name must not be empty", exception.getMessage());
    }

    @Test
    void readCsv_shouldThrow_whenFileNameIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> csvReader.readCsv("", false));

        assertEquals("File name must not be empty", exception.getMessage());
    }

    @Test
    void readCsv_shouldThrowFileNotFoundException_whenFileDoesNotExist() {
        FileNotFoundException exception = assertThrows(FileNotFoundException.class,
                () -> csvReader.readCsv("nonexistent.csv", false));

        assertTrue(exception.getMessage().contains("nonexistent.csv"));
    }

    @Test
    void readCsv_shouldSkipBlankLines() throws IOException {
        List<String> lines = csvReader.readCsv(withBlankLinesFilePath, true);

        lines.forEach(line -> assertFalse(line.isBlank()));
    }
}
