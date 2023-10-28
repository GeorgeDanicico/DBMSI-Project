package com.dbms.project.exceptions;

public class DBMSException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;

    public DBMSException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public DBMSException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = "UNEXPECTED ERROR";
    }
}