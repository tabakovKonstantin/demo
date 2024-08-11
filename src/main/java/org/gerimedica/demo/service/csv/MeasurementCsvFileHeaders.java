package org.gerimedica.demo.service.csv;

public enum MeasurementCsvFileHeaders {
    SOURCE("source"),
    CODE_LIST_CODE("codeListCode"),
    CODE("code"),
    DISPLAY_VALUE("displayValue"),
    LONG_DESCRIPTION("longDescription"),
    FROM_DATE("fromDate"),
    TO_DATE("toDate"),
    SORTING_PRIORITY("sortingPriority");

    MeasurementCsvFileHeaders(String source) {

    }
}
