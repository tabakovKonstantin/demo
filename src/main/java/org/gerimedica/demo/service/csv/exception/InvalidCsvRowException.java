package org.gerimedica.demo.service.csv.exception;

public class InvalidCsvRowException extends RuntimeException {
    public InvalidCsvRowException(String message) {
        super(message);
    }
}
