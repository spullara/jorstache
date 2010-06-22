package com.github.jorstache;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* Future that can't be cancelled.
* <p/>
* User: sam
* Date: Jun 21, 2010
* Time: 5:34:01 PM
*/
public abstract class UncancellableFuture<T> implements Future<T> {
  private boolean done;

  @Override
  public boolean cancel(boolean b) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return done;
  }

  protected void setDone() {
    done = true;
  }

  @Override
  public abstract T get() throws InterruptedException, ExecutionException;

  @Override
  public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    throw new NotImplementedException();
  }
}
