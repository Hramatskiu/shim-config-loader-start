package com.epam.spring.exception;

public class CommonUtilException extends Exception {
  public CommonUtilException() {
    super();
  }

  public CommonUtilException( String message ) {
    super( message );
  }

  public CommonUtilException( String message, Throwable cause ) {
    super( message, cause );
  }

  public CommonUtilException( Throwable cause ) {
    super( cause );
  }

  protected CommonUtilException( String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
