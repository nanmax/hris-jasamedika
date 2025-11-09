package com.nanmax.hris.security;
import com.nanmax.hris.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthenticationHolder authenticationHolder;
    public JwtAuthenticationFilter(JwtService jwtService, AuthenticationHolder authenticationHolder) {
        this.jwtService = jwtService;
        this.authenticationHolder = authenticationHolder;
    }
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
            return;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtService.validateToken(token);
        if (claims == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }
        AuthenticationContext context = new AuthenticationContext();
        context.setIdUser(claims.getSubject());
        context.setProfile((String) claims.get("profile"));
        context.setNamaLengkap((String) claims.get("namaLengkap"));
        context.setEmail((String) claims.get("email"));
        context.setKdDepartemen((Integer) claims.get("kdDepartemen"));
        context.setNamaDepartemen((String) claims.get("namaDepartemen"));
        authenticationHolder.setCurrentUser(context);
        try {
            filterChain.doFilter(request, response);
        } finally {
            authenticationHolder.clear();
        }
    }
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/combo/") ||
               path.startsWith("/vaadin/") ||
               path.startsWith("/frontend/") ||
               path.startsWith("/VAADIN/") ||
               path.equals("/") ||
               path.startsWith("/h2-console");
    }
}