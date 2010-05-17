package com.googlecode.jorstache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheCompiler;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import jornado.ErrorResponse;
import jornado.Handler;
import jornado.Request;
import jornado.Response;

import java.io.File;
import java.util.HashMap;

/**
 * Handle a mustache template request.
 * <p/>
 * User: sam
 * Date: May 16, 2010
 * Time: 5:01:24 PM
 */
public class MustacheHandler implements Handler<Request> {
  private MustacheCompiler mc;
  private Mustache mustache;
  private long timestamp;
  private File template;
  private String path;
  private long lastcheck = System.currentTimeMillis();

  public MustacheHandler(File root, String path) throws MustacheException {
    this.path = path;
    template = new File(root, path);
    timestamp = template.lastModified();
    mc = new MustacheCompiler(root);
    mustache = mc.parseFile(this.path);
  }

  @Override
  public Response handle(final Request request) {
    // Periodically check to see if the file has changed
    if (System.currentTimeMillis() - lastcheck > 10000) {
      if (template.lastModified() != timestamp) {
        lastcheck = timestamp = template.lastModified();
        try {
          mustache = mc.parseFile(path);
        } catch (MustacheException e) {
          // Log an error but continue running
          e.printStackTrace();
        }
      }
    }
    Object parameterScope = new HashMap<String, String>() {
      @Override
      public String get(Object o) {
        return request.getPathParameter(o.toString());
      }
    };

    Object requestScope = new Object() {
      String method() {
        return request.getMethod().name();
      }

      String path() {
        return request.getPath();
      }

      String url() {
        return request.getReconstructedUrl();
      }

      String referrer() {
        return request.getReferer();
      }

      String id() {
        return request.getRequestId();
      }

      String xsrf() {
        return request.getXsrfCookie();
      }

      boolean isLoggedIn() {
        return request.isLoggedIn();
      }

      HashMap<String, String> header() {
        return new HashMap<String, String>() {
          @Override
          public String get(Object o) {
            return request.getHeader(o.toString());
          }
        };
      }

      HashMap<String, String> param() {
        return new HashMap<String, String>() {
          @Override
          public String get(Object o) {
            return request.getParameter(o.toString());
          }
        };
      }

      HashMap<String, String> cookie() {
        return new HashMap<String, String>() {
          @Override
          public String get(Object o) {
            return request.getCookieValue(o.toString());
          }
        };
      }
    };
    Scope scope = new Scope(parameterScope, new Scope(requestScope));
    try {
      return new MustacheResponse(mustache, scope);
    } catch (MustacheException e) {
      return new ErrorResponse("Failed to execute mustache: " + mustache.getPath());
    }
  }
}
