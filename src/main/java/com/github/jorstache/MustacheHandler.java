package com.github.jorstache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheCompiler;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.RuntimeJavaCompiler;
import jornado.ErrorResponse;
import jornado.Handler;
import jornado.Request;
import jornado.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle a mustache template request.
 * <p/>
 * User: sam
 * Date: May 16, 2010
 * Time: 5:01:24 PM
 */
public abstract class MustacheHandler implements Handler<Request> {

  private Logger logger = Logger.getLogger("MustacheHandler");
  private MustacheCompiler mc;
  private Mustache mustache;
  private long timestamp;
  protected File template;
  protected File code;
  protected String path;
  private String codePath;
  private String classname;
  private long lastcheck = System.currentTimeMillis();
  protected Class clazz;
  private long codetimestamp;

  public MustacheHandler(File root, String path) throws MustacheException {
    this.path = path;
    template = new File(root, path);
    timestamp = template.lastModified();
    mc = new MustacheCompiler(root);
    mustache = mc.parseFile(this.path);
  }

  public MustacheHandler(File root, String templatePath, String codePath, String classname) throws MustacheException {
    this.path = templatePath;
    this.codePath = codePath;
    this.classname = classname;
    template = new File(root, templatePath);
    code = new File(root, codePath);
    timestamp = template.lastModified();
    codetimestamp = code.lastModified();
    mc = new MustacheCompiler(root);
    mc.setSuperclass("com.github.jorstache.Jorstache");
    mustache = mc.parseFile(this.path);
    try {
      clazz = Class.forName(classname);
      code = null;
    } catch (Exception ce) {
      try {
        clazz = RuntimeJavaCompiler.compile(new PrintWriter(System.out, true), classname, getText(codePath, new BufferedReader(new FileReader(code)))).loadClass(classname);
      } catch (Exception e) {
        throw new MustacheException("Failed to read code: " + codePath, e);
      }
    }
  }

  private static String getText(String template, BufferedReader br) {
    StringBuilder text = new StringBuilder();
    String line;
    try {
      while ((line = br.readLine()) != null) {
        text.append(line);
        text.append("\n");
      }
      br.close();
    } catch (IOException e) {
      throw new AssertionError("Failed to read template file: " + template);
    }
    return text.toString();
  }

  public Response handle(Object scope) {
    // Periodically check to see if the file has changed
    if (System.currentTimeMillis() - lastcheck > 10000) {
      if (template.lastModified() != timestamp) {
        lastcheck = timestamp = template.lastModified();
        try {
          mustache = mc.parseFile(path);
        } catch (MustacheException e) {
          // Log an error but continue running
          logger.log(Level.SEVERE, "Failed to parse: " + path, e);
        }
      }
    }
    try {
      return new MustacheResponse(mustache, new Scope(scope));
    } catch (MustacheException e) {
      return new ErrorResponse("Failed to execute mustache: " + mustache.getPath());
    }
  }

  public Class<?> getCode() {
    if (code != null && System.currentTimeMillis() - lastcheck > 10000) {
      if (code.exists() && code.lastModified() != codetimestamp) {
        lastcheck = codetimestamp = code.lastModified();
        try {
          clazz = RuntimeJavaCompiler.compile(new PrintWriter(System.out, true), classname, getText(codePath, new BufferedReader(new FileReader(code)))).loadClass(classname);
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Could not compile " + codePath, e);
        }
      }
    }
    return clazz;
  }
}
