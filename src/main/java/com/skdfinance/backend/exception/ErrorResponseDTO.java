package com.skdfinance.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The shape every error response takes, whatever exception caused it.
 * fieldErrors is only populated for @Valid failures — null otherwise,
 * and Jackson will omit it from the JSON when null (see application.properties
 * if you want that made explicit with spring.jackson.default-property-inclusion=non_null).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;
}
