package com.sampullara.fred;

import com.google.inject.Injector;
import jornado.Request;
import jornado.UserService;

/**
 * Fred scope
 * <p/>
 * User: sam
 * Date: May 17, 2010
 * Time: 2:54:05 PM
 */
public class Fred {
  Request request;
  Injector injector;

  public Fred(Request request, Injector injector) {
    this.request = request;
    this.injector = injector;
  }
  
  String first_name = "Fred";
  String last_name = "Flintstone";
  String webid() {
    return injector.getInstance(UserService.class).load(null).getWebId();
  }
  String url() { return request.getReconstructedUrl(); }
}
