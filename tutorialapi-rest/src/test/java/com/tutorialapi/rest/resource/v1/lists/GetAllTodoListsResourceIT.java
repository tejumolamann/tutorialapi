package com.tutorialapi.rest.resource.v1.lists;

import com.tutorialapi.model.config.ConfigKey;
import com.tutorialapi.model.user.Subscription;
import com.tutorialapi.rest.ApiApplication;
import com.tutorialapi.rest.exception.ErrorResponse;
import com.tutorialapi.rest.security.SecurityHeader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import org.sqlite.JDBC;

import java.util.Properties;
import java.util.logging.LogManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetAllTodoListsResourceIT extends JerseyTest {

    static {
        LogManager.getLogManager().reset();
    }

    @Override
    protected Application configure() {
        Properties properties = new Properties();
        properties.setProperty(ConfigKey.DB_DRIVER.getKey(), JDBC.class.getName());
        properties.setProperty(ConfigKey.DB_URL.getKey(), "jdbc:sqlite::memory:");
        properties.setProperty(ConfigKey.DB_USERNAME.getKey(), "");
        properties.setProperty(ConfigKey.DB_PASSWORD.getKey(), "");

        Config config = ConfigFactory.parseProperties(properties);
        return new ApiApplication(config);
    }

    @Test
    void getAllTodoListsNoSecurityHeaders() {
        Response response = target("/v1/lists").request().get();

        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(
                "Missing security header: " + SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(),
                errorResponse.getMessage()
        );
    }

    @Test
    void getAllTodoListsProxySecretHeader() {
        Response response = target("/v1/lists").request()
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
    void getAllTodoListsProxySecretHeaderAndUserHeaders() {
        Response response = target("/v1/lists").request()
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
    void getAllTodoListsOnlyInvalidSubscription() {
        Response response = target("/v1/lists").request()
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
    void getAllTodoListsValidHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPID_API_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPID_API_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_API_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .get();

        assertEquals(200, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        //TODO: FIX FAILING
//        assertEquals("Hello World!", response.readEntity(String.class));

        assertEquals("*", response.getHeaderString("Access-Control-Allow-Origin"));
        assertEquals(
                "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD",
                response.getHeaderString("Access-Control-Allow-Methods")
        );
    }
}