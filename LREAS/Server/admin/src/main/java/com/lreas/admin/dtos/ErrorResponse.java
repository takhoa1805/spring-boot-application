package com.lreas.admin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private Map<String, String> error;

    public static ErrorResponse of(String message) {
        return new ErrorResponse(Map.of("message", message));
    }
}
