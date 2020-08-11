package io.github.skarware.exchangerates.filters;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
 * A filter to run an Angular project that uses router and act as single-page application.
 * This solution avoids any external dependencies like necessity to rewrite rules on HTTP Server.
 */
@Component
public class URLRewriteFilter implements Filter {

    private final String API_PATTERN = "^\\/api\\/(.+)$";
    private final String POINT_EXCLUSION_PATTERN = "^([^.]+)$";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String requestURI = servletRequest.getRequestURI();
        String contextPath = servletRequest.getContextPath();
        if (!requestURI.equals(contextPath) &&
                !requestURI.matches(API_PATTERN) && // Check if the requested URL is not a controller (/api/**)
                requestURI.matches(POINT_EXCLUSION_PATTERN) // Check if there are no "." in requested URL
        ) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/");
            dispatcher.forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

}