package com.github.jorstache;

import jornado.Request;
import jornado.UserService;

public class CodeBehindFred {
  Request request;

  public CodeBehindFred(Request request) {
    this.request = request;
  }

  String first_name = "Fred";
  String last_name = "Flintstone";
  String webid() {
    return JorstacheServer.injector.getInstance(UserService.class).load(null).getWebId();
  }
  String url() { return request.getReconstructedUrl(); }
}
