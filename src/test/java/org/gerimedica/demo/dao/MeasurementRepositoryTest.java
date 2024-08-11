package org.gerimedica.demo.dao;

import org.gerimedica.demo.domain.entity.Measurement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MeasurementRepositoryTest {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Test
    void whenFindByCode_thenReturnMeasurement() {

        //given
        var code = "testCode";
        var measurement =   new Measurement();
        measurement.setCode(code);
        measurement.setSource("test");
        measurementRepository.save(measurement);

        //when
        var result = measurementRepository.findByCode(code);

        //then
        assertNotNull(result);
        assertEquals(measurement.getCode(), result.getCode());
    }

    @Test
    void whenNotFindByCode_thenReturnNull() {

        //given
        var code = "testCode";
        var measurement =   new Measurement();
        measurement.setCode(code);
        measurement.setSource("test");
        measurementRepository.save(measurement);

        //when
        var result = measurementRepository.findByCode("anotherCode");

        //then
        assertNull(result);
    }
}