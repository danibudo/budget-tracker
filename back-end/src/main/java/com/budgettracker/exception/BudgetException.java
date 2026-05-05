package com.budgettracker.exception;

public class BudgetException extends RuntimeException {

    private final ErrorCode errorCode;
    private final int httpStatus;

    public BudgetException(String message, ErrorCode errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public ErrorCode getErrorCode() { return errorCode; }
    public int getHttpStatus() { return httpStatus; }
}