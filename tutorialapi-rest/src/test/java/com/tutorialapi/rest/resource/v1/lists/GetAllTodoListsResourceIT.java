package com.tutorialapi.rest.resource.v1.lists;

import com.tutorialapi.db.ServiceFactory;
import com.tutorialapi.db.service.TodoListService;
import com.tutorialapi.model.TodoList;
import com.tutorialapi.model.user.RapidApiPrincipal;
import com.tutorialapi.model.user.Subscription;
import com.tutorialapi.rest.ApiApplication;
import com.tutorialapi.rest.resource.v1.BaseResourceIT;
import com.tutorialapi.rest.security.SecurityHeader;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetAllTodoListsResourceIT extends BaseResourceIT {

    private TodoListService todoListService;

    @Override
    protected Application configure() {
        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        todoListService = Mockito.mock(TodoListService.class);
        Mockito.when(serviceFactory.getTodoListService()).thenReturn(todoListService);

        return new ApiApplication(serviceFactory);
    }

    @Test
    void testNoSecurityHeaders() {
        Response response = target("/v1/lists").request().get();

        verifyErrorResponse(
                response,
                Response.Status.UNAUTHORIZED.getStatusCode(),
                "Missing security header: X-RapidAPI-Proxy-Secret"
        );
    }

    @Test
    void testOnlyProxySecretHeader() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .get();

        verifyErrorResponse(
                response,
                Response.Status.UNAUTHORIZED.getStatusCode(),
                "Missing security header: X-RapidAPI-User"
        );
    }

    @Test
    void testProxySecretHeaderAndUserHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .get();

        verifyErrorResponse(
                response, Response.Status.UNAUTHORIZED.getStatusCode(),
                "Missing or invalid security header: X-RapidAPI-Subscription"
        );
    }

    @Test
    void testInvalidSubscription() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(), "invalid-subscription")
                .get();

        verifyErrorResponse(
                response, Response.Status.UNAUTHORIZED.getStatusCode(),
                "Missing or invalid security header: X-RapidAPI-Subscription"
        );
    }

    @Test
    void testNoTodoLists() {
        RapidApiPrincipal principal = new RapidApiPrincipal("user", Subscription.BASIC, "proxy-secret");
        Mockito.when(todoListService.getAll(ArgumentMatchers.eq(principal))).thenReturn(Collections.emptyList());

        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .get();

        assertEquals(200, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        List<TodoList> results = response.readEntity(new GenericType<>() {});

        assertTrue(results.isEmpty());
        verifyCorsHeaders(response);
    }

    @Test
    void testSomeTodoLists() {
        List<TodoList> lists = List.of(
                new TodoList().setId("id1").setName("name1"), new TodoList().setId("id2").setName("name2")
        );

        RapidApiPrincipal principal = new RapidApiPrincipal("user", Subscription.BASIC, "proxy-secret");
        Mockito.when(todoListService.getAll(ArgumentMatchers.eq(principal))).thenReturn(lists);

        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .get();

        assertEquals(200, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        List<TodoList> results = response.readEntity(new GenericType<>() {});

        assertEquals(lists, results);
        verifyCorsHeaders(response);
    }

    @Test
    void testServiceException() {
        RapidApiPrincipal principal = new RapidApiPrincipal("user", Subscription.BASIC, "proxy-secret");
        Mockito.when(todoListService.getAll(ArgumentMatchers.eq(principal))).thenThrow(new RuntimeException("Failed"));

        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .get();

        verifyErrorResponse(response, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Failed");
    }
}