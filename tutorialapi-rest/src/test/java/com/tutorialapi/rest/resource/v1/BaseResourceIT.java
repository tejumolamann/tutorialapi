package com.tutorialapi.rest.resource.v1;

import com.tutorialapi.model.config.ConfigKey;
import com.tutorialapi.rest.ApiApplication;
import com.tutorialapi.rest.exception.ErrorResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.sqlite.JDBC;

import java.util.Properties;
import java.util.logging.LogManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseResourceIT extends JerseyTest {

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