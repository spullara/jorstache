package com.github.jorstache;

import com.sampullara.util.FutureWriter;
import jornado.Body;
import jornado.RenderService;

import java.io.IOException;
import java.io.Writer;

/**
 * Trivial render service for Mustache bodies.
 * <p/>
 * User: sam
 * Date: May 16, 2010
 * Time: 2:07:44 PM
 */
public class MustacheRenderer implements RenderService {
  @Override
  public void write(Writer writer, Body body) throws IOException {
    MustacheBody mb = (MustacheBody) body;
    FutureWriter futureWriter = mb.getFutureWriter();
    futureWriter.setWriter(writer);
    futureWriter.flush();
  }
}
