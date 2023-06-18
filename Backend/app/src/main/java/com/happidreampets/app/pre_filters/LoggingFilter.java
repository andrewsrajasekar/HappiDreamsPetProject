package com.happidreampets.app.pre_filters;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoggingFilter implements Filter{
    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;
                Long startTime = System.currentTimeMillis();
                LOG.log(Level.INFO, "Logging Request  {0} : {1}", new Object[]{req.getMethod(), req.getRequestURI()});
                chain.doFilter(request, response);
                LOG.log(Level.INFO, "Logging Response : {0}", new Object[]{res.getContentType()});
                LOG.log(Level.INFO, "Request Completion Time : {0}ms", new Object[]{System.currentTimeMillis() - startTime});
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
    
    
}
