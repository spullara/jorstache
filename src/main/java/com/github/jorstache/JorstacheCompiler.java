package com.github.jorstache;

import com.sampullara.mustache.MustacheBuilder;

import java.io.File;

/**
* Created by IntelliJ IDEA.
* User: sam
* Date: 4/11/11
* Time: 5:46 PM
* To change this template use File | Settings | File Templates.
*/
public class JorstacheCompiler extends MustacheBuilder {
  private static boolean DEBUG = Boolean.getBoolean("jorstache.debug");

  public JorstacheCompiler(File root) {
    super(root);
  }

}
