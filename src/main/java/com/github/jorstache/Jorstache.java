package com.github.jorstache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheCompiler;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Our own implementation of Mustache for caching and reloading partial evaluations
 * <p/>
 * User: sam
 * Date: May 27, 2010
 * Time: 5:10:24 PM
 */
public abstract class Jorstache extends Mustache {

  // TODO: Guice up mustache.java
  private static ExecutorService es = Executors.newCachedThreadPool();

  static class TimestampedMustache {
    long timestamp;
    Mustache mustache;
    long lastcheck;
  }

  private Map<String, TimestampedMustache> cache = new ConcurrentHashMap<String, TimestampedMustache>();

  @Override
  protected void partial(final FutureWriter writer, Scope s, String name) throws MustacheException {
    TimestampedMustache tm = cache.get(name);
    String filename = name + ".html";
    if (tm == null || ((System.currentTimeMillis() - tm.lastcheck > 10000) &&
            ((tm.lastcheck = System.currentTimeMillis()) > 0) &&
            (new File(getRoot(), filename).lastModified() > tm.timestamp))) {
      MustacheCompiler c = new MustacheCompiler(getRoot());
      if (name != null) {
        Mustache mustache = c.parseFile(filename);
        tm = new TimestampedMustache();
        tm.mustache = mustache;
        tm.timestamp = new File(getRoot(), filename).lastModified();
        tm.lastcheck = System.currentTimeMillis();
        cache.put(name, tm);
      } else {
        return;
      }
    }
    Object parent = s.get(name);
    final Scope scope = parent == null ? s : new Scope(parent, s);
    Integer timeout = (Integer) scope.get("timeout");
    Long startTime = (Long) scope.get("jorstacheStartTime");
    if (timeout == null || startTime == null) {
      tm.mustache.execute(writer, scope);
      return;
    }
    final long timeoutMillis = timeout - (System.currentTimeMillis() - startTime);
    final TimestampedMustache finalTm = tm;
    final Future<Object> future = es.submit(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        FutureWriter fw = new FutureWriter();
        finalTm.mustache.execute(fw, scope);
        return fw;
      }
    });

    try {
      writer.enqueue(new UncancellableFuture<Object>() {
        @Override
        public boolean isDone() {
          if (super.isDone()) {
            return true;
          }
          return future.isDone();
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
          try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
          } catch (TimeoutException e) {
            FutureWriter fw = new FutureWriter();
            Object o = scope.get("timedout");
            if (o != null) {
              try {
                fw.enqueue(o.toString());
              } catch (IOException e1) {
                throw new ExecutionException("Closed", e1);
              }
            }
            FutureWriter timedoutWriter = (FutureWriter) scope.get("timedoutWriter");
            if (timedoutWriter != null) {
              try {
                timedoutWriter.enqueue(future);
              } catch (IOException e1) {
                throw new ExecutionException("Closed", e1);
              }
            }
            return fw;
          } finally {
            setDone();
          }
        }
      });
    } catch (IOException e) {
      throw new MustacheException("Closed", e);
    }
  }

}
