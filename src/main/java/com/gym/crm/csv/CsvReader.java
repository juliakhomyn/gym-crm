package com.gym.crm.csv;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvReader {
    private static final String EMPTY_FILE_NAME_EXCEPTION = "File name must not be empty";
    private static final String FILE_NOT_FOUND_EXCEPTION = "File not found: %s";
    private static final String READ_FROM_FILE_ERROR = "Failed to read from file: %s";

    public List<String> readCsv(String fileName, boolean isHeader) throws IOException, IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_FILE_NAME_EXCEPTION);
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(String.format(FILE_NOT_FOUND_EXCEPTION, fileName));
        } catch (IOException e) {
            throw new IOException(String.format(READ_FROM_FILE_ERROR, fileName), e);
        }

        return lines;
    }
}
