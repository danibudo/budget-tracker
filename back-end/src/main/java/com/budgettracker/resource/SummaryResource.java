package com.budgettracker.resource;

import com.budgettracker.service.SummaryService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/summary")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SummaryResource {

    @Inject
    private SummaryService summaryService;

    @GET
    public Response getSummary() {
        return Response.ok(summaryService.getSummary()).build();
    }

    @GET
    @Path("/expenses-by-category")
    public Response getExpensesByCategory() {
        return Response.ok(summaryService.getExpensesByCategory()).build();
    }

    @GET
    @Path("/monthly")
    public Response getMonthly() {
        return Response.ok(summaryService.getMonthly()).build();
    }
}