package com.happidreampets.app.pre_filters;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class LoggingFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        CharResponseWrapper wrappedResponse = new CharResponseWrapper(res);
        Long startTime = System.currentTimeMillis();
        LOG.log(Level.INFO, "Logging Request  {0} : {1}", new Object[] { req.getMethod(), req.getRequestURI() });
        chain.doFilter(request, response);
        LOG.log(Level.INFO, "Logging Response : {0}", new Object[] { wrappedResponse.getResponseBody() });
        LOG.log(Level.INFO, "Request Completion Time : {0}ms", new Object[] { System.currentTimeMillis() - startTime });
    }

    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private final CharArrayWriter writer = new CharArrayWriter();

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(writer);
        }

        public String getResponseBody() {
            return writer.toString();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
