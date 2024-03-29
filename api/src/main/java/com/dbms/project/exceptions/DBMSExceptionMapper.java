package com.dbms.project.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DBMSExceptionMapper extends ResponseEntityExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(DBMSExceptionMapper.class);

        @ExceptionHandler({ DBMSException.class })
        protected ResponseEntity<Object> handleException(
                RuntimeException ex, WebRequest request) {
            String bodyOfResponse = ex.getMessage();
            return handleExceptionInternal(ex, bodyOfResponse,
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        }
}
