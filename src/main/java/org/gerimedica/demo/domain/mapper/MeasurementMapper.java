package org.gerimedica.demo.domain.mapper;

import org.gerimedica.demo.domain.dto.Measurement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MeasurementMapper {

    MeasurementMapper INSTANCE = Mappers.getMapper(MeasurementMapper.class);

    org.gerimedica.demo.domain.entity.Measurement toEntity(Measurement measurement);
    Measurement toDto(org.gerimedica.demo.domain.entity.Measurement measurement);

    List<org.gerimedica.demo.domain.entity.Measurement> toEntityList(List<Measurement> measurement);
    List<Measurement> toDtoList(List<org.gerimedica.demo.domain.entity.Measurement> measurement);
}
