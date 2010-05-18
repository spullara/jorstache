package com.github.jorstache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;
import jornado.Body;
import jornado.HeaderOp;
import jornado.Response;
import jornado.Status;

import java.util.ArrayList;

/**
 * Create a mustache response.
 * <p/>
 * User: sam
 * Date: May 16, 2010
 * Time: 2:04:48 PM
 */
public class MustacheResponse implements Response {
  public static final Iterable<HeaderOp> EMPTY_ITERABLE = new ArrayList<HeaderOp>(0);
  private FutureWriter fw;

  public MustacheResponse(Mustache mustache, Scope scope) throws MustacheException {
    fw = new FutureWriter();
    mustache.execute(fw, scope);
  }

  @Override
  public Iterable<HeaderOp> getHeaderOps() {
    return EMPTY_ITERABLE;
  }

  @Override
  public Status getStatus() {
    return Status.OK;
  }

  @Override
  public Body getBody() {
    return new MustacheBody(fw);
  }

}
