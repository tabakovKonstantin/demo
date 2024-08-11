package org.gerimedica.demo.controller;

import org.gerimedica.demo.domain.dto.Measurement;
import org.gerimedica.demo.service.MeasurementService;
import org.gerimedica.demo.service.csv.CsvFileReader;
import org.gerimedica.demo.service.csv.exception.FileValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MeasurementController.class)
class MeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeasurementService measurementService;

    @MockBean
    private CsvFileReader csvFileReader;

    public static final String CSV_DATA = """
            source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority
            source1,codeList1,code1,display1,longDesc1,11-08-2024,12-08-2024,1
            source2,codeList2,code2,display2,longDesc2,11-09-2024,12-09-2024,2
            """;

    private List<Measurement> mockMeasurements = List.of(
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

    @Test
    void testUpload() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "measurements.csv",
                "text/csv", CSV_DATA.getBytes());

        when(csvFileReader.readCsv(any())).thenReturn(mockMeasurements);
        when(measurementService.saveMeasurements(mockMeasurements)).thenReturn(mockMeasurements);

        mockMvc.perform(multipart("/measurement")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(mockMeasurements.size())))
                .andExpect(jsonPath("$[0].code", is("code1")))
                .andExpect(jsonPath("$[1].code", is("code2")));

        verify(csvFileReader, only()).readCsv(any());
        verify(measurementService, only()).saveMeasurements(mockMeasurements);
    }

    @Test
    void testUpload_emptyFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "measurements.csv",
                "text/csv", "".getBytes());

        mockMvc.perform(multipart("/measurement")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("File validation error")))
                .andExpect(jsonPath("$.message", is("File is empty")));

    }

    @Test
    void testUpload_invalidFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "measurements.csv",
                "text/csv", CSV_DATA.getBytes());

        when(csvFileReader.readCsv(any())).thenThrow(new FileValidationException("test"));

        mockMvc.perform(multipart("/measurement")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("File validation error")))
                .andExpect(jsonPath("$.message", is("test")));

        verify(csvFileReader, only()).readCsv(any());
    }

    @Test
    void testUpload_unexpectedException() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "measurements.csv",
                "text/csv", CSV_DATA.getBytes());

        when(csvFileReader.readCsv(any())).thenThrow(new RuntimeException("test"));

        mockMvc.perform(multipart("/measurement")
                        .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Internal Server Error")));

        verify(csvFileReader, only()).readCsv(any());
    }


    @Test
    void testGetAll() throws Exception {
        when(measurementService.getMeasurements()).thenReturn(mockMeasurements);

        mockMvc.perform(get("/measurement"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(mockMeasurements.size())))
                .andExpect(jsonPath("$[0].code", is("code1")))
                .andExpect(jsonPath("$[1].code", is("code2")));

        verify(measurementService, only()).getMeasurements();
    }

    @Test
    void testGetByCode() throws Exception {
        var mockMeasurement = mockMeasurements.getFirst();
        when(measurementService.getByCode(mockMeasurement.code())).thenReturn(mockMeasurement);

        mockMvc.perform(get("/measurement/code1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.source", is(mockMeasurement.source())))
                .andExpect(jsonPath("$.codeListCode", is(mockMeasurement.codeListCode())))
                .andExpect(jsonPath("$.code", is(mockMeasurement.code())))
                .andExpect(jsonPath("$.displayValue", is(mockMeasurement.displayValue())))
                .andExpect(jsonPath("$.longDescription", is(mockMeasurement.longDescription())))
                .andExpect(jsonPath("$.fromDate", is("11-08-2024")))
                .andExpect(jsonPath("$.toDate", is("12-08-2024")))
                .andExpect(jsonPath("$.sortingPriority", is(mockMeasurement.sortingPriority())));

        verify(measurementService, only()).getByCode(mockMeasurement.code());
    }

    @Test
    void testGetByCode_notFound() throws Exception {
        var mockMeasurement = mockMeasurements.getFirst();
        when(measurementService.getByCode(mockMeasurement.code())).thenReturn(null);

        mockMvc.perform(get("/measurement/code1"))
                .andExpect(status().isNotFound());

        verify(measurementService, only()).getByCode(mockMeasurement.code());
    }

    @Test
    void testDeleteAll() throws Exception {
        mockMvc.perform(delete("/measurement"))
                .andExpect(status().isNoContent());

        verify(measurementService, only()).deleteMeasurements();
    }

}