package com.tutorialapi.rest.resource;

import com.tutorialapi.rest.ApiApplication;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestResourceTest extends JerseyTest {

//    static {
//        LogManager.getLogManager().reset();
//    }

    @Override
    protected Application configure() {
        return new ApiApplication();
    }

    @Test
    void test() {
        Response response = target("/test").request().get();

        assertEquals(200, response.getStatus());
    }
}