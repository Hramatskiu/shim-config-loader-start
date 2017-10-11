package com.epam.spring.aspect;

import com.epam.spring.authenticate.impl.TestConfigLoadCredentials;
import com.epam.spring.config.SshCredentials;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SshSecurityAspect {
  @Pointcut( "execution(* com.epam.spring.util.SshCommonUtil.*(..))" )
  public void addSshSecurity() {
  }

  @Around( "addSshSecurity()" )
  public String setHttpSecurityToClientBuilder( ProceedingJoinPoint joinPoint ) throws Throwable {
    Object[] args = joinPoint.getArgs();
    setupSecurityArgs( args, getCredentialsFromSecurityContext().getSshCredentials() );

    return (String) joinPoint.proceed( args );
  }

  private TestConfigLoadCredentials getCredentialsFromSecurityContext() throws Exception {
    Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
    if ( loggedAuthentication instanceof TestConfigLoadCredentials ) {
      return (TestConfigLoadCredentials) loggedAuthentication;
    }

    throw new Exception( "tt" );
  }

  //Change to dto wrapping
  private void setupSecurityArgs( Object[] args, SshCredentials sshCredentials ) {
    if ( args[ 0 ] instanceof String ) {
      args[ 0 ] = sshCredentials.getUsername();
    }

    if ( args[ 1 ] instanceof String ) {
      args[ 1 ] = sshCredentials.getPassword();
    }

    if ( args[ 5 ] instanceof String ) {
      args[ 5 ] = sshCredentials.getIdentityPath();
    }
  }
}
