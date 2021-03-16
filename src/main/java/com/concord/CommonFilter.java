package com.concord;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CommonFilter implements Filter {

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    servletRequest.setCharacterEncoding("UTF-8");
    long requestTime = System.currentTimeMillis();
    HttpServletRequest wrappedRequest;
    if ((servletRequest.getContentType() == null)
        || !servletRequest.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
      // Enable logging for json only
      wrappedRequest = (HttpServletRequest) servletRequest;
    } else {
      CachedStreamHttpServletRequest cachedRequest =
          new CachedStreamHttpServletRequest((HttpServletRequest) servletRequest);
      wrappedRequest = cachedRequest;
    }
    final HttpServletResponse cachingResponse =
        (servletRequest instanceof ContentCachingRequestWrapper)
            ? (HttpServletResponse) servletResponse
            : new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);
    CommonFilterLogger logger =
        new CommonFilterLogger((HttpServletRequest) servletRequest, requestTime);
    logger.logRequest(true);
    filterChain.doFilter(wrappedRequest, cachingResponse);
    try {
      logger.logResponse(cachingResponse);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
