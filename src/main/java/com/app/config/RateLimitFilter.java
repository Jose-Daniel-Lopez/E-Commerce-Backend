package com.app.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final int LIMIT = 100; // 100 requests
    private static final Duration DURATION = Duration.ofMinutes(1); // In a 1-minute window

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket4j.builder()
                .addLimit(Bandwidth.classic(LIMIT, Refill.greedy(LIMIT, DURATION)))
                .build());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest req && response instanceof HttpServletResponse res) {
            String ip = req.getRemoteAddr();
            Bucket bucket = resolveBucket(ip);
            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                res.setStatus(429);
                res.getWriter().write("Too Many Requests. Please try again later.");
                return;
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}

