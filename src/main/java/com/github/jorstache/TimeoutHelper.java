package com.github.jorstache;

import com.sampullara.util.FutureWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Help with things that timeout.
 * <p/>
 * User: sam
 * Date: Jun 21, 2010
 * Time: 4:22:51 PM
 */
public class TimeoutHelper {
  private AtomicInteger id = new AtomicInteger(0);
  private Map<Integer, FutureWriter> map = new ConcurrentHashMap<Integer, FutureWriter>();

  protected void put(FutureWriter fw) {
    map.put(id.intValue(), fw);
  }

  protected int id() {
    return id.incrementAndGet();
  }

  protected FutureWriter timedoutWriter() {
    FutureWriter writer = new FutureWriter();
    put(writer);
    return writer;
  }

  protected String timedout() {
    return "<div id='timeout" + id() + "'></div>";
  }

  protected Future<String> queue() throws IOException {
    return new UncancellableFuture<String>() {
      @Override
      public String get() throws InterruptedException, ExecutionException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, FutureWriter> entry : map.entrySet()) {
          FutureWriter fw = entry.getValue();
          int id = entry.getKey();
          // Timed out
          StringWriter sw = new StringWriter();
          fw.setWriter(sw);
          try {
            fw.flush();
          } catch (IOException e) {
            throw new ExecutionException("Closed", e);
          }
          sb.append("<script>document.getElementById('timeout")
                  .append(id)
                  .append("').innerHTML = '")
                  .append(encode(sw.toString()))
                  .append("';</script>");
        }
        return sb.toString();
      }
    };
  }

  private String encode(String actual) {
    return actual.replace("'", "\\'").replace("\n", "\\n").replace("&", "&amp;");
  }
}
