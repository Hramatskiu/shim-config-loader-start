package com.epam.loader.common.delegating.executor;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DelegatingExecutorService implements Closeable {
  private ExecutorService executorService;

  public DelegatingExecutorService( int threadsCount ) {
    this.executorService = Executors.newFixedThreadPool( threadsCount );
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }

  @Override public void close() throws IOException {
    if ( !executorService.isShutdown() ) {
      executorService.shutdownNow();
    }
  }
}
