package org.mifos.connector.common.interceptor.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.StreamUtils;

/**
 * Smoke test for the Jakarta EE migration: confirms the servlet wrapper (now built on jakarta.servlet.*) still buffers
 * the request body so it can be read more than once.
 */
class MultiReadHttpServletRequestTest {

    @Test
    void bodyCanBeReadMultipleTimes() throws Exception {
        String payload = "{\"hello\":\"world\"}";
        MockHttpServletRequest delegate = new MockHttpServletRequest();
        delegate.setContent(payload.getBytes(StandardCharsets.UTF_8));

        MultiReadHttpServletRequest request = new MultiReadHttpServletRequest(delegate);

        String firstRead = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String secondRead = request.getReader().lines().reduce("", String::concat);

        assertEquals(payload, firstRead);
        assertEquals(payload, secondRead);
    }
}
