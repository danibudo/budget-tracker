package com.budgettracker.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BudgetExceptionMapper implements ExceptionMapper<BudgetException> {

    @Override
    public Response toResponse(BudgetException ex) {
        return Response
                .status(ex.getHttpStatus())
                .entity(new ErrorResponse(ex.getMessage(), ex.getErrorCode().name()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}