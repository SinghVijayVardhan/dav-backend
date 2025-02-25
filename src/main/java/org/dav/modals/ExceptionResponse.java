package org.dav.modals;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class ExceptionResponse {
    HttpStatus httpStatus;
    String errorMessage;
    Integer statusCode;
}
