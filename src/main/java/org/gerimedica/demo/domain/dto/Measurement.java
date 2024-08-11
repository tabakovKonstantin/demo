package org.gerimedica.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Measurement(String source,
                          String codeListCode,
                          @NotBlank(message = "Code cannot be blank")
                          String code,
                          String displayValue,
                          String longDescription,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                          LocalDate fromDate,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                          LocalDate toDate,
                          Integer sortingPriority
) {
}
