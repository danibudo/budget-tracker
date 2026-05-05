package com.budgettracker.resource;

import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.service.TransactionService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/transactions")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    private TransactionService transactionService;

    @GET
    public Response getAll(@QueryParam("from") String from, @QueryParam("to") String to) {
        return Response.ok(transactionService.getAll(from, to)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(transactionService.getById(id)).build();
    }

    @POST
    public Response create(TransactionRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(transactionService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, TransactionRequest request) {
        return Response.ok(transactionService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        transactionService.delete(id);
        return Response.noContent().build();
    }
}