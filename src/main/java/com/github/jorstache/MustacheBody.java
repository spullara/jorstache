package com.github.jorstache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;
import jornado.Body;
import jornado.MediaType;

import java.io.IOException;
import java.io.Writer;

/**
* Hold onto the FutureWriter for the Renderer to use.
* <p/>
* User: sam
* Date: May 16, 2010
* Time: 2:13:47 PM
*/
public class MustacheBody implements Body {
  private Mustache mustache;
  private Scope scope;

  public MustacheBody(Mustache mustache, Scope scope) {
    this.mustache = mustache;
    this.scope = scope;
  }

  @Override
  public Class<MustacheRenderer> getRenderServiceClass() {
    return MustacheRenderer.class;
  }

  @Override
  public MediaType getMediaType() {
    return MediaType.TEXT_HTML_UTF8;
  }

  public void execute(Writer writer) throws MustacheException, IOException {
    FutureWriter fw = new FutureWriter(writer);
    mustache.execute(fw, scope);
    fw.flush();
  }
}
