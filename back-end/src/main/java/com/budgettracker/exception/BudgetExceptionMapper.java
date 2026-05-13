package com.budgettracker.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class BudgetExceptionMapper implements ExceptionMapper<BudgetException> {

    private static final Logger log = Logger.getLogger(BudgetExceptionMapper.class.getName());

    @Override
    public Response toResponse(BudgetException ex) {
        log.warning("BudgetException [" + ex.getErrorCode().name() + "] status=" + ex.getHttpStatus() + " message=" + ex.getMessage());
        return Response
                .status(ex.getHttpStatus())
                .entity(new ErrorResponse(ex.getMessage(), ex.getErrorCode().name()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}