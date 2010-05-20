package com.github.jorstache;

import jornado.Request;
import jornado.RequestFactory;

/**
 * TODO: Edit this
 * <p/>
 * User: sam
 * Date: May 20, 2010
 * Time: 11:30:32 AM
 */
public class JorstacheRequestFactory implements RequestFactory<JorstacheUser, JorstacheRequest> {
  @Override
  public JorstacheRequest createRequest(Request<JorstacheUser> jorstacheUserRequest) {
    return new JorstacheRequest(jorstacheUserRequest);
  }
}
