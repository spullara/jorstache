package com.github.jorstache;

import com.sampullara.mustache.MustacheException;
import jornado.ErrorResponse;
import jornado.Request;
import jornado.Response;

import java.io.File;

/**
* The code behind handler combines code (possibly dynamic) and a template.
* <p/>
* User: sam
* Date: May 17, 2010
* Time: 4:55:24 PM
*/
public class CodebehindHandler extends MustacheHandler {
  public CodebehindHandler(File root, String templatePath, String codePath, String classname) throws MustacheException {
    super(root, templatePath, codePath, classname);
  }

  public CodebehindHandler(File root, String templatePath, Class clazz) throws MustacheException {
    super(root, templatePath);
    this.clazz = clazz;
  }

  @Override
  public Response handle(Request request) {
    try {
      Object o = getCode().getConstructor(Request.class).newInstance(request);
      return super.handle(o);
    } catch (Exception e) {
      e.printStackTrace();
      return new ErrorResponse(e.getMessage());
    }
  }
}
