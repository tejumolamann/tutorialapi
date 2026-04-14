package com.tutorialapi.rest.security;

import com.tutorialapi.model.user.RapidApiPrincipal;
import com.tutorialapi.model.user.Subscription;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessLogFilterTest {
    @Test
    void testFilterNoUser() {
        List<String> logList = new ArrayList<>();
        AccessLogFilter accessLogFilter = new AccessLogFilter(logList::add);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePath()).thenReturn(URI.create("https://localhost/test"));
        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getMethod()).thenReturn("GET");
        when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);
        accessLogFilter.filter(containerRequestContext);

        assertEquals(1, logList.size());
        assertEquals("? => GET /test", logList.get(0));
    }

    @Test
    void testFilterWithUser() {
        List<String> logList = new ArrayList<>();
        AccessLogFilter accessLogFilter = new AccessLogFilter(logList::add);

        RapidApiPrincipal principal = new RapidApiPrincipal("user", Subscription.BASIC, "proxy-secret");
        RapidApiSecurityContext securityContext = new RapidApiSecurityContext(principal);
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePath()).thenReturn(URI.create("https://localhost/test"));
        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getSecurityContext()).thenReturn(securityContext);
        when(containerRequestContext.getMethod()).thenReturn("GET");
        when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);
        accessLogFilter.filter(containerRequestContext);

        assertEquals(1, logList.size());
        assertEquals("user => GET /test", logList.get(0));
    }
}