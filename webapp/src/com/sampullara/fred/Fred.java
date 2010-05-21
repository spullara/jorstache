package com.sampullara.fred;

import jornado.Request;
import jornado.UserService;
import com.google.inject.Inject;

/**
 * Fred scope
 * <p/>
 * User: sam
 * Date: May 17, 2010
 * Time: 2:54:05 PM
 */
public class Fred {
  @Inject
  Request request;
  @Inject
  UserService userservice;

  String first_name = "Fred";
  String last_name = "Flintstone";

  String webid() {
    return userservice.load("spullara").getWebId();
  }

  String url() {
    return request.getReconstructedUrl();
  }
}
