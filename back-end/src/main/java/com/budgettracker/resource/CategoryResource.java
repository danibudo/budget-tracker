package com.budgettracker.resource;

import com.budgettracker.dto.CategoryRequest;
import com.budgettracker.service.CategoryService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/categories")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private static final Logger log = Logger.getLogger(CategoryResource.class.getName());

    @Inject
    private CategoryService categoryService;

    @GET
    public Response getAll(@QueryParam("type") String type) {
        log.info("GET /categories type=" + type);
        return Response.ok(categoryService.getAll(type)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        log.info("GET /categories/" + id);
        return Response.ok(categoryService.getById(id)).build();
    }

    @POST
    public Response create(CategoryRequest request) {
        log.info("POST /categories name=" + request.name());
        return Response.status(Response.Status.CREATED)
                .entity(categoryService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, CategoryRequest request) {
        log.info("PUT /categories/" + id + " name=" + request.name());
        return Response.ok(categoryService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE /categories/" + id);
        categoryService.delete(id);
        return Response.noContent().build();
    }
}