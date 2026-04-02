package com.finance.dashboard.filter;

import com.finance.dashboard.config.RateLimiterConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Applies per-IP, per-path token-bucket rate limiting before the JWT filter.
 * Returns HTTP 429 with a JSON body when the bucket is exhausted.
 *
 * Path groups (see RateLimiterConfig for limits):
 *   /api/auth/login  → 5 / min
 *   /api/records/**  → 30 / min
 *   /api/dashboard/**→ 60 / min
 */
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiterConfig rateLimiterConfig;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String ip     = getClientIp(request);
        Bucket bucket = resolveBucket(request.getRequestURI(), ip);

        if (bucket == null || bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        writeTooManyRequestsResponse(response, request.getRequestURI());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Extracts the real client IP, handling proxies via X-Forwarded-For. Complexity: 2 */
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    /** Maps the request URI to the appropriate per-IP bucket, or null for unlimited paths. Complexity: 3 */
    private Bucket resolveBucket(String uri, String ip) {
        if (uri.equals("/api/auth/login"))    return rateLimiterConfig.resolveLoginBucket(ip);
        if (uri.startsWith("/api/records"))   return rateLimiterConfig.resolveRecordsBucket(ip);
        if (uri.startsWith("/api/dashboard")) return rateLimiterConfig.resolveDashboardBucket(ip);
        return null;
    }

    /** Writes a structured HTTP 429 JSON response without requiring ObjectMapper. Complexity: 1 */
    private void writeTooManyRequestsResponse(HttpServletResponse response, String uri)
            throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":429,\"error\":\"Too Many Requests\"," +
                "\"message\":\"Rate limit exceeded. Please try again later.\",\"path\":\"%s\"}",
                LocalDateTime.now(), uri
        );

        response.getWriter().write(body);
    }
}
