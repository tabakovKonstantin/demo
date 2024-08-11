package org.gerimedica.demo.dao;

import org.gerimedica.demo.domain.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    Measurement findByCode(String code);
    List<Measurement> findAllByCodeIn(List<String> code);
}
