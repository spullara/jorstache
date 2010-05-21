package com.github.jorstache;

import com.google.inject.Inject;
import jornado.Request;
import jornado.UserService;

public class CodeBehindFred {
  @Inject
  Request request;
  @Inject UserService userService;

  String first_name = "Fred";
  String last_name = "Flintstone";
  String webid() {
    return userService.load("test").getWebId();
  }
  String url() { return request.getReconstructedUrl(); }
}
