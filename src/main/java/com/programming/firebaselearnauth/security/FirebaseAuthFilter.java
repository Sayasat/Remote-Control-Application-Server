package com.programming.firebaselearnauth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.programming.firebaselearnauth.dto.response.VerifyTokenResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FirebaseAuthFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if ("/verify-token".equals(request.getRequestURI())) {
            if (token != null) {
                try {
                    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                    if (decodedToken != null) {
                        VerifyTokenResponse tokenResponse = new VerifyTokenResponse(true);
                        sendJsonResponse(response, tokenResponse);
                        return;
                    }
                } catch (Exception e) {
                    VerifyTokenResponse tokenResponse = new VerifyTokenResponse(false);
                    sendJsonResponse(response, tokenResponse);
                    return;
                }
            } else {
                VerifyTokenResponse tokenResponse = new VerifyTokenResponse(false);
                sendJsonResponse(response, tokenResponse);
                return;
            }
        }

        if (token != null) {
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                if (decodedToken != null) {
                    SecurityContextHolder.getContext().setAuthentication(new FirebaseAuthenticationToken(decodedToken));
                }
            } catch (Exception e) {
                handleInvalidToken(response);
                return;
            }
        }

        if (token == null) {
            handleInvalidToken(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        VerifyTokenResponse tokenResponse = new VerifyTokenResponse(false);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        sendJsonResponse(response, tokenResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendJsonResponse(HttpServletResponse response, VerifyTokenResponse tokenResponse) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }
}


