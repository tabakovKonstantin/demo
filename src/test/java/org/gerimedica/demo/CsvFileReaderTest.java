package org.gerimedica.demo;

import org.gerimedica.demo.domain.dto.Measurement;
import org.gerimedica.demo.service.csv.CsvFileReader;
import org.gerimedica.demo.service.csv.exception.FileValidationException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvFileReaderTest {
    private final CsvFileReader csvFileReader = new CsvFileReader();

    @Test
    public void testReadCsvWithGeneratedData() {
        //given
        String csvData = """
                source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority
                source1,codeList1,code1,display1,longDesc1,11-08-2024,12-08-2024,1
                source2,codeList2,code2,display2,longDesc2,11-09-2024,12-09-2024,2
                """;
        var inputStream = new ByteArrayInputStream(csvData.getBytes());

        //when
        var actualMeasurements = csvFileReader.readCsv(inputStream);

        //then
        var expectedMeasurements = List.of(
                new Measurement(
                        "source1", "codeList1", "code1", "display1", "longDesc1",
                        LocalDate.parse("11-08-2024", CsvFileReader.DATE_TIME_FORMATTER),
                        LocalDate.parse("12-08-2024", CsvFileReader.DATE_TIME_FORMATTER),
                        1
                ),
                new Measurement(
                        "source2", "codeList2", "code2", "display2", "longDesc2",
                        LocalDate.parse("11-09-2024", CsvFileReader.DATE_TIME_FORMATTER),
                        LocalDate.parse("12-09-2024", CsvFileReader.DATE_TIME_FORMATTER),
                        2
                )
        );

        assertEquals(expectedMeasurements, actualMeasurements);
    }

    @Test
    public void testReadCsvWithGeneratedData_invalidData() {
        //given
        String csvData = """
                source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority
                source1,codeList1,,display1,longDesc1,11-08-2024,31o-12-2019,1
                source2,codeList2,code2,display2,longDesc2,11-09-2024,12-09-2024,2t
                """;
        var inputStream = new ByteArrayInputStream(csvData.getBytes());

        //then
        var exception = assertThrows(FileValidationException.class, () -> csvFileReader.readCsv(inputStream));
        assertNotNull(exception.getMessage());
    }


}