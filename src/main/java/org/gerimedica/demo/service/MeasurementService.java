package org.gerimedica.demo.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.gerimedica.demo.DuplicateCodeException;
import org.gerimedica.demo.dao.MeasurementRepository;
import org.gerimedica.demo.domain.dto.Measurement;
import org.gerimedica.demo.domain.mapper.MeasurementMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    @Transactional
    public List<Measurement> saveMeasurements(List<Measurement> measurements) {
        var entities = MeasurementMapper.INSTANCE.toEntityList(measurements);
        var existedEntities = measurementRepository.findAllByCodeIn(measurements.stream().map(Measurement::code).toList());
        if (!existedEntities.isEmpty()) {
            var codes = existedEntities.stream().map(org.gerimedica.demo.domain.entity.Measurement::getCode).collect(Collectors.joining(","));
            throw new DuplicateCodeException("There are codes in system %s".formatted(codes));
        }
        var savedEntities = measurementRepository.saveAll(entities);
        return MeasurementMapper.INSTANCE.toDtoList(savedEntities);
    }

    public List<Measurement> getMeasurements() {
        var entities = measurementRepository.findAll();
        return MeasurementMapper.INSTANCE.toDtoList(entities);
    }

    public Measurement getByCode(String code) {
        var entity = measurementRepository.findByCode(code);
        return MeasurementMapper.INSTANCE.toDto(entity);
    }

    public void deleteMeasurements() {
        measurementRepository.deleteAll();
    }
}
