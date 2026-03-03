package org.agora.occ.dto.common;

import lombok.Data;

import java.util.List;

/**
 * Error response details containing validation errors.
 */
@Data
public class ValidationErrorResponse {

    private List<String> errors;
}
