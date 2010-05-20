package com.github.jorstache;

import jornado.WebUser;

/**
 * Jorstache User
 * <p/>
 * User: sam
 * Date: May 20, 2010
 * Time: 11:31:43 AM
 */
public class JorstacheUser implements WebUser {
  private String id;

  public JorstacheUser(String id) {
    this.id = id;
  }

  @Override
  public String getWebId() {
    return id;
  }
}
