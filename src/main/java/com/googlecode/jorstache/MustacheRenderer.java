package com.googlecode.jorstache;

import jornado.AbstractIncrementalRenderService;
import jornado.Body;

/**
 * Trivial render service for Mustache bodies.
 * <p/>
 * User: sam
 * Date: May 16, 2010
 * Time: 2:07:44 PM
 */
public class MustacheRenderer extends AbstractIncrementalRenderService {
  @Override
  public Iterable<byte[]> render(Body body) {
    MustacheBody mb = (MustacheBody) body;
    return mb.getFutureWriter();
  }
}
