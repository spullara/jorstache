package com.github.jorstache;

import com.sampullara.mustache.MustacheCompiler;

import java.io.File;

/**
* Created by IntelliJ IDEA.
* User: sam
* Date: 4/11/11
* Time: 5:46 PM
* To change this template use File | Settings | File Templates.
*/
public class JorstacheCompiler extends MustacheCompiler {
  public JorstacheCompiler(File root) {
    super(root);
  }

  @Override
  protected void writeText(StringBuilder sb, String text) {
    super.writeText(sb, text.replaceAll("\\s+", " ").replaceAll("^ ", ""));
  }
}
