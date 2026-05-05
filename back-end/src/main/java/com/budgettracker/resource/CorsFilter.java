package com.budgettracker.resource;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
}
