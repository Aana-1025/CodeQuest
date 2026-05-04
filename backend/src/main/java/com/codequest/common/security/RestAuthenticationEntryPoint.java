package com.codequest.common.security;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.codequest.common.dto.ErrorDTO;
import com.codequest.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorDTO errorDTO = new ErrorDTO(
                Instant.now(),
                HttpServletResponse.SC_UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED.name(),
                "Unauthorized.",
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorDTO));
    }
}
