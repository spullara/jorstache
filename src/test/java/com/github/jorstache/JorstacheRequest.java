package com.github.jorstache;

import jornado.Request;
import jornado.RequestWrapper;

/**
 * Jorstache request
 * <p/>
 * User: sam
 * Date: May 20, 2010
 * Time: 11:30:49 AM
 */
public class JorstacheRequest extends RequestWrapper<JorstacheUser> {
  public JorstacheRequest(Request<JorstacheUser> delegate) {
    super(delegate);
  }
}
