package com.concord;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class CachedStreamHttpServletRequest extends HttpServletRequestWrapper {
  @Getter private final boolean jsonType;
  @Getter @Setter private byte[] rawData;

  CachedStreamHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    jsonType = request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE);
    rawData = ByteStreams.toByteArray(request.getInputStream());
  }

  @Override
  public ServletInputStream getInputStream() {
    CachedServletInputStream servletStream =
        new CachedServletInputStream(new ByteArrayInputStream(rawData));
    return servletStream;
  }

  @Override
  public BufferedReader getReader(){
    return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(rawData)));
  }

  private static class CachedServletInputStream extends ServletInputStream {

    private final InputStream stream;

    CachedServletInputStream(InputStream stream) {
      this.stream = stream;
    }

    @Override
    public int read() throws IOException {
      return stream.read();
    }

    @Override
    public boolean isFinished() {
      try {
        return stream.available() == 0;
      } catch (IOException ex) {
        log.error("", ex);
        return true;
      }
    }

    @Override
    public boolean isReady() {
      try {
        return stream.available() > 0;
      } catch (IOException ex) {
        log.error("", ex);
        return false;
      }
    }

    @Override
    public void setReadListener(ReadListener listener) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
