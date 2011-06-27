package com.github.jorstache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import jornado.Body;
import jornado.HeaderOp;
import jornado.Response;
import jornado.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a mustache response.
 * <p/>
 * User: sam
 * Date: May 16, 2010
 * Time: 2:04:48 PM
 */
public class MustacheResponse implements Response {
  public static final Iterable<HeaderOp> EMPTY_ITERABLE = new ArrayList<HeaderOp>(0);
  private List<HeaderOp> headerList = new ArrayList<HeaderOp>();
  private MustacheBody mustacheBody;
  private Status status = Status.OK;

  public MustacheResponse(Mustache mustache, Scope scope) throws MustacheException {
    scope.put("jorstacheStartTime", System.currentTimeMillis());
    mustacheBody = new MustacheBody(mustache, scope);
  }

  @Override
  public Iterable<HeaderOp> getHeaderOps() {
    return headerList;
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public Body getBody() {
    return mustacheBody;
  }

  @Override
  public void addHeaderOp(HeaderOp headerOp) {
    headerList.add(headerOp);
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
