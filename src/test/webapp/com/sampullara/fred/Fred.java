package com.sampullara.fred;

import jornado.Request;
import jornado.UserService;
import com.github.jorstache.JorstacheServer;
import com.google.inject.Inject;

/**
 * Fred scope
 * <p/>
 * User: sam
 * Date: May 17, 2010
 * Time: 2:54:05 PM
 */
public class Fred {
  final Request request;

  @Inject
  public Fred(Request request) {
    this.request = request;
  }

  String first_name = "Fred";
  String last_name = "Flintstone";

  String webid() {
    return JorstacheServer.injector.getInstance(UserService.class).load("fred").getWebId();
  }

  String url() {
    return request.getReconstructedUrl();
  }
}
