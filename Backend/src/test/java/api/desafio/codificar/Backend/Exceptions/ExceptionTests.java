package api.desafio.codificar.Backend.Exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionTests {

    @Test
    void apiErrorConstructorsAndAccessorsWork() {
        LocalDateTime now = LocalDateTime.now();
        ApiError error = new ApiError();
        error.setTimestamp(now);
        error.setStatus(418);
        error.setError("error");
        error.setMessage("message");
        error.setPath("/path");

        assertEquals(now, error.getTimestamp());
        assertEquals(418, error.getStatus());
        assertEquals("error", error.getError());
        assertEquals("message", error.getMessage());
        assertEquals("/path", error.getPath());

        ApiError complete = new ApiError(now, 400, "bad", "problem", "/complete");
        assertEquals(400, complete.getStatus());
    }

    @Test
    void exceptionConstructorsKeepMessagesAndCauses() {
        Throwable cause = new IllegalStateException("cause");
        assertEquals("business", new BusinessException("business").getMessage());
        assertEquals("missing", new ResourceNotFoundException("missing").getMessage());
        assertSame(cause, new ResourceNotFoundException("missing", cause).getCause());
        assertSame(cause, new ResourceNotFoundException(cause).getCause());
    }

    @Test
    void globalHandlerMapsAllExceptionTypes() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/resource");

        var notFound = handler.handleException(new ResourceNotFoundException("missing"), request);
        var business = handler.handleException(new BusinessException("invalid"), request);
        var internal = handler.handleException(new Exception("failure"), request);

        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatusCode());
        assertEquals(404, notFound.getBody().getStatus());
        assertEquals("/resource", notFound.getBody().getPath());
        assertEquals(HttpStatus.BAD_REQUEST, business.getStatusCode());
        assertEquals("invalid", business.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, internal.getStatusCode());
        assertEquals("failure", internal.getBody().getMessage());
    }
}
