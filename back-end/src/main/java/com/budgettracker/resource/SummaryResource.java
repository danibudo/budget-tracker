package com.budgettracker.resource;

import com.budgettracker.service.SummaryService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/summary")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SummaryResource {

    private static final Logger log = Logger.getLogger(SummaryResource.class.getName());

    @Inject
    private SummaryService summaryService;

    @GET
    public Response getSummary() {
        log.info("GET /summary");
        return Response.ok(summaryService.getSummary()).build();
    }

    @GET
    @Path("/expenses-by-category")
    public Response getExpensesByCategory() {
        log.info("GET /summary/expenses-by-category");
        return Response.ok(summaryService.getExpensesByCategory()).build();
    }

    @GET
    @Path("/monthly")
    public Response getMonthly() {
        log.info("GET /summary/monthly");
        return Response.ok(summaryService.getMonthly()).build();
    }
}