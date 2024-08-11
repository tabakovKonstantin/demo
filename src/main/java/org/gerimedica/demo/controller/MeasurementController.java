package org.gerimedica.demo.controller;

import lombok.AllArgsConstructor;
import org.gerimedica.demo.domain.dto.Measurement;
import org.gerimedica.demo.service.MeasurementService;
import org.gerimedica.demo.service.csv.CsvFileReader;
import org.gerimedica.demo.service.csv.exception.FileValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/measurement")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final CsvFileReader csvFileReader;


    @PostMapping(consumes = "multipart/form-data")
    public List<Measurement> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new FileValidationException("File is empty");
        }
        var measurements = csvFileReader.readCsv(file.getInputStream());
        return measurementService.saveMeasurements(measurements);
    }

    @GetMapping
    public List<Measurement> getAll() {
        return measurementService.getMeasurements();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Measurement> getByCode(@PathVariable String code) {
        var res = measurementService.getByCode(code);
        if (res == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        measurementService.deleteMeasurements();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
