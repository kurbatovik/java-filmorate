package ru.yandex.practicum.filmorate.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MDCFilter extends OncePerRequestFilter {

    private static int track;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String[] queries = request.getServletPath().split("/");
        MDC.put("trackingNumber", ++track + "");
        MDC.put("query", queries[1]);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("trackingNumber");
            MDC.remove("query");
        }
    }
}