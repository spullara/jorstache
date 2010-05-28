package com.github.jorstache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheCompiler;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Our own implementation of Mustache for caching and reloading partial evaluations
 * <p/>
 * User: sam
 * Date: May 27, 2010
 * Time: 5:10:24 PM
 */
public abstract class Jorstache extends Mustache {
  static class TimestampedMustache {
    long timestamp;
    Mustache mustache;
    long lastcheck;
  }

  private Map<String, TimestampedMustache> cache = new ConcurrentHashMap<String, TimestampedMustache>();

  @Override
  protected void partial(FutureWriter writer, Scope s, String name) throws MustacheException {
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
    Scope scope = parent == null ? s : new Scope(parent, s);
    tm.mustache.execute(writer, scope);
  }
}
