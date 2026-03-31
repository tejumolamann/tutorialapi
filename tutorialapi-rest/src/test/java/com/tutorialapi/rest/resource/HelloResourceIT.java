package com.tutorialapi.rest.resource;

import com.tutorialapi.model.Subscription;
import com.tutorialapi.rest.ApiApplication;
import com.tutorialapi.rest.exception.ErrorResponse;
import com.tutorialapi.rest.security.SecurityHeader;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import java.util.logging.LogManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloResourceIT extends JerseyTest {

    static {
        LogManager.getLogManager().reset();
    }

    @Override
    protected Application configure() {
        return new ApiApplication();
    }

    @Test
    void testNoSecurityHeaders() {
        Response response = target("/test").request().get();

        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(
                "Missing security header: " + SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(),
                errorResponse.getMessage()
        );
    }

    @Test
    void testProxySecretHeader() {
        Response response = target("/test").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .get();

        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(
                "Missing security header: " + SecurityHeader.RAPID_API_USER.getHeader(),
                errorResponse.getMessage()
        );
    }

    @Test
    void testProxySecretHeaderAndUserHeaders() {
        Response response = target("/test").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .get();

        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(
                "Missing security header: " + SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(),
                errorResponse.getMessage()
        );
    }

    @Test
    void testOnlyInvalidSubscription() {
        Response response = target("/test").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(), "invalid-subscription")
                .get();

        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(
                "Missing security header: " + SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(),
                errorResponse.getMessage()
        );
    }

    @Test
    void testValidFeaders() {
        Response response = target("/test").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .get();

        assertEquals(200, response.getStatus());
        assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
        assertEquals("Hello World!", response.readEntity(String.class));

        assertEquals("*", response.getHeaderString("Access-Control-Allow-Origin"));
        assertEquals(
                "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD",
                response.getHeaderString("Access-Control-Allow-Methods")
        );
    }
}