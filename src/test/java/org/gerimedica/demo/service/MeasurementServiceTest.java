package org.gerimedica.demo.service;

import org.gerimedica.demo.DuplicateCodeException;
import org.gerimedica.demo.domain.dto.Measurement;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MeasurementServiceTest {

    @Autowired
    private MeasurementService measurementService;

    @Test
    void saveMeasurements_ShouldSaveAndReturnSavedMeasurements() {
        // given
        var dtoList = Instancio.ofList(Measurement.class).size(3).create();

        //when
        var res = measurementService.saveMeasurements(dtoList);

        //then
        assertNotNull(res);
        var actual = res.getFirst();
        var expected = dtoList.getFirst();
        assertNotNull(actual);
        assertEquals(expected.fromDate(), actual.fromDate());
        assertEquals(expected.toDate(), actual.toDate());
        assertEquals(expected.code(), actual.code());
    }

    @Test
    void saveMeasurements_ShouldSaveAndReturnSavedMeasurements1() {
        // given
        var dtoList = Instancio.ofList(Measurement.class).size(3).create();
        measurementService.saveMeasurements(dtoList);

        //when
        var exception = assertThrows(DuplicateCodeException.class, () -> measurementService.saveMeasurements(dtoList));
        assertNotNull(exception.getMessage());
    }

}