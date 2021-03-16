package com.concord; // package com.concord;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
class CommonFilterLogger {

  private static final int MAX_RESPONSE_BODY_SIZE = 2_000;
  private static final String ARRAY_OPEN_BRACKET = " [";

  private final HttpServletRequest request;
  private final long startTime;
  private String url;

  CommonFilterLogger(HttpServletRequest request, long startTime) {
    this.request = request;
    this.startTime = startTime;
  }

  void logRequest(boolean logData) throws IOException {

    String requestData = "";

    byte[] input;
    if (logData) {
      input = ByteStreams.toByteArray(request.getInputStream());
    } else {
      input = "".getBytes();
    }
    requestData += getBody(input, Integer.MAX_VALUE);
    if (!requestData.isEmpty()) {
      requestData = ARRAY_OPEN_BRACKET + requestData + ']';
    }
    url =
        request.getRequestURI()
            + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    log.info(" -> {} {}{}", request.getMethod(), url, requestData);
  }

  void logResponse(final HttpServletResponse servletResponse) throws Exception {
    ContentCachingResponseWrapper wrapper =
        WebUtils.getNativeResponse(servletResponse, ContentCachingResponseWrapper.class);
    String responseData = "";

    if (wrapper != null) {
      if ((servletResponse.getContentType() != null)
          && (servletResponse.getContentType().startsWith("application/json"))) {
        responseData +=
            getBody(
                wrapper.getContentAsByteArray(), MAX_RESPONSE_BODY_SIZE - responseData.length());
      }

      wrapper.copyBodyToResponse();
    }
    if (!responseData.isEmpty()) {
      responseData = ARRAY_OPEN_BRACKET + responseData + ']';
    }
    String encryptioned = Util.encrypt(responseData);
    log.info("encryption: {}", encryptioned);
    log.info("decryption: {}", Util.decrypt(encryptioned));
    log.info(
        " <- {} {} {}{} ({} ms)",
        request.getMethod(),
        url,
        servletResponse.getStatus(),
        responseData,
        System.currentTimeMillis() - startTime);
  }

  private String getBody(final byte[] data, final int maxSize) {
    int size = Math.min(data.length, maxSize);
    String bodyStr = new String(data, 0, size).replace('\n', ' ');
    return bodyStr;
  }

  public void error(String message) {
    log.error(message);
  }
}
