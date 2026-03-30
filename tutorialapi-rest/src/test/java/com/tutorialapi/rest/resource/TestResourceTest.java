package com.tutorialapi.rest.resource;

import com.tutorialapi.rest.ApiApplication;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.LogManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestResourceTest extends JerseyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestResourceTest.class);

    static {
        LogManager.getLogManager().reset();
    }

    @Override
    protected Application configure() {
        return new ApiApplication();
    }

    @Test
    void test() {
        Response response = target("/test").request().get();

        assertEquals(200, response.getStatus());
        assertEquals("Hello World!", response.readEntity(String.class));

        assertEquals("*", response.getHeaderString("Access-Control-Allow-Origin"));
        assertEquals(
                "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD",
                response.getHeaderString("Access-Control-Allow-Methods")
        );
    }
}