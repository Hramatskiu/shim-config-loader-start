package com.epam.spring.aspect;

import com.epam.spring.authenticate.impl.TestConfigLoadCredentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class Krb5SecurityAspect {
  @Pointcut( "@annotation(com.epam.spring.annotation.SecurityAnnotation)" )
  public void accessSecureCluster() {
  }

  @SuppressWarnings( { "unchecked", "ConstantConditions" } )
  @Around( "accessSecureCluster()" )
  public CompletableFuture<Boolean> aroundKerberosAccess( ProceedingJoinPoint joinPoint ) throws Throwable {
    return isSecuritySet() ? (CompletableFuture<Boolean>) UserGroupInformation.getLoginUser()
      .doAs( (PrivilegedExceptionAction<Object>) () -> {
        try {
          return joinPoint.proceed();
        } catch ( Throwable throwable ) {
          throw (Exception) throwable;
        }
      } ) : (CompletableFuture<Boolean>) joinPoint.proceed();
  }

  private boolean isSecuritySet() {
    return getCredentialsFromSecurityContext().isKerberosSet();
  }

  private TestConfigLoadCredentials getCredentialsFromSecurityContext() throws AuthenticationException {
    Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
    if ( loggedAuthentication instanceof TestConfigLoadCredentials ) {
      return (TestConfigLoadCredentials) loggedAuthentication;
    }

    throw new BadCredentialsException( "Another authentication!" );
  }
}
