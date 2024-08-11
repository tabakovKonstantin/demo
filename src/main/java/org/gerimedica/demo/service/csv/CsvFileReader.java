package org.gerimedica.demo.service.csv;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.gerimedica.demo.domain.dto.Measurement;
import org.gerimedica.demo.service.csv.exception.FileValidationException;
import org.gerimedica.demo.service.csv.exception.InvalidCsvRowException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsvFileReader {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public List<Measurement> readCsv(InputStream inputStream) {
        var csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(MeasurementCsvFileHeaders.class)
                .setSkipHeaderRecord(true)
                .build();

        var reader = new BufferedReader(new InputStreamReader(inputStream));

        Iterable<CSVRecord> records = null;
        try {
            records = csvFormat.parse(reader);
        } catch (IOException e) {
            log.error("Error reading file", e);
            return List.of();
        }

        List<Measurement> measurements = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        for (var record : records) {
            try {
                var measurement = getMeasurement(record);
                measurements.add(measurement);
            } catch (InvalidCsvRowException e) {
                violations.add(e.getMessage());
            }
        }

        var countUniqRecords = measurements.stream().map(Measurement::code).distinct().count();
        if (countUniqRecords != measurements.size()) {
            violations.add("The file contains non-unique elements.");
        }

        if (!violations.isEmpty()) {
            var msg = violations.stream().collect(Collectors.joining(System.lineSeparator()));
            throw new FileValidationException(msg);
        }

        return measurements;

    }

    private Measurement getMeasurement(CSVRecord record) {
        var source = record.get(MeasurementCsvFileHeaders.SOURCE);
        var codeListCode = record.get(MeasurementCsvFileHeaders.CODE_LIST_CODE);
        var code = record.get(MeasurementCsvFileHeaders.CODE);
        var displayValue = record.get(MeasurementCsvFileHeaders.DISPLAY_VALUE);
        var longDescription = record.get(MeasurementCsvFileHeaders.LONG_DESCRIPTION);
        var fromDate = record.get(MeasurementCsvFileHeaders.FROM_DATE);
        var toDate = record.get(MeasurementCsvFileHeaders.TO_DATE);
        var sortingPriority = record.get(MeasurementCsvFileHeaders.SORTING_PRIORITY);

        if(code.isBlank()) {
            var msg = "Error parsing line number %d. Code cannot be blank".formatted(record.getRecordNumber());
            throw new InvalidCsvRowException(msg);
        }

        Measurement measurement;
        try {
            measurement = new Measurement(source,
                    codeListCode.isBlank() ? null : codeListCode,
                    code,
                    displayValue.isBlank() ? null : displayValue,
                    longDescription.isBlank() ? null : longDescription,
                    fromDate.isBlank() ? null : LocalDate.parse(fromDate, DATE_TIME_FORMATTER),
                    toDate.isBlank() ? null : LocalDate.parse(toDate, DATE_TIME_FORMATTER),
                    sortingPriority.isBlank() ? null : Integer.valueOf(sortingPriority));
        } catch (NumberFormatException | DateTimeParseException e) {
            var msg = "Error parsing line number %d. Incorrect format: %s".formatted(record.getRecordNumber(), e.getMessage());
            throw new InvalidCsvRowException(msg);
        }
        return measurement;
    }
}
