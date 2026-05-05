package com.budgettracker.resource;

import com.budgettracker.dto.CategoryRequest;
import com.budgettracker.service.CategoryService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/categories")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    private CategoryService categoryService;

    @GET
    public Response getAll(@QueryParam("type") String type) {
        return Response.ok(categoryService.getAll(type)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(categoryService.getById(id)).build();
    }

    @POST
    public Response create(CategoryRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(categoryService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, CategoryRequest request) {
        return Response.ok(categoryService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        categoryService.delete(id);
        return Response.noContent().build();
    }
}