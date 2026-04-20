package com.tutorialapi.rest.resource.v1.lists;

import com.tutorialapi.db.ServiceFactory;
import com.tutorialapi.model.TodoList;
import com.tutorialapi.model.user.RapidApiPrincipal;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/v1/lists")
public class GetAllTodoListsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllTodoListsResource.class);

    private final ServiceFactory serviceFactory;

    @Inject
    public GetAllTodoListsResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        LOGGER.info("ServiceFactory injected: {}", serviceFactory);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TodoList> getAllTodoLists(@Context SecurityContext securityContext) {
        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();

        return serviceFactory.getTodoListService().getAll(principal);
    }
}
