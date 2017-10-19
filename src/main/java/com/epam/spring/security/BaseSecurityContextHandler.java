package com.epam.spring.security;

import com.epam.spring.security.authenticate.impl.ConfigLoadCredentials;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseSecurityContextHandler {
  protected static ConfigLoadCredentials getCredentialsFromSecurityContext() throws AuthenticationException {
    Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
    if ( loggedAuthentication instanceof ConfigLoadCredentials ) {
      return (ConfigLoadCredentials) loggedAuthentication;
    }

    throw new BadCredentialsException( "Another authentication!" );
  }
}
