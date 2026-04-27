package com.tutorialapi.rest.resource.v1;

import com.tutorialapi.rest.exception.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;

import java.util.logging.LogManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseResourceIT extends JerseyTest {

    static {
        LogManager.getLogManager().reset();
    }

    protected void verifyErrorResponse(Response response, int expectedStatus, String expectedMessage) {
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(expectedStatus, errorResponse.getStatus());
        assertEquals(expectedMessage, errorResponse.getMessage());

        verifyCorsHeaders(response);
    }

    protected void verifyCorsHeaders(Response response) {
        assertEquals("*", response.getHeaderString("Access-Control-Allow-Origin"));
        assertEquals(
                "DELETE, HEAD, GET, OPTIONS, PATCH, POST, PUT",
                response.getHeaderString("Access-Control-Allow-Methods")
        );
    }
}