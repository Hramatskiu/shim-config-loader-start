package com.epam.spring.aspect;

import com.epam.loader.config.credentials.SshCredentials;
import com.epam.spring.security.BaseSecurityContextHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class SshSecurityAspect extends BaseSecurityContextHandler {
  @Pointcut( "execution(* com.epam.loader.common.util.SshCommonUtil.*(..))" )
  public void addSshSecurity() {
  }

  @Around( "addSshSecurity()" )
  public String setHttpSecurityToClientBuilder( ProceedingJoinPoint joinPoint ) throws Throwable {
    Object[] args = joinPoint.getArgs();
    setupSecurityArgs( args );

    return (String) joinPoint.proceed( args );
  }

  //Change to dto wrapping
  private void setupSecurityArgs( Object[] args ) {
    Object credentials =
      Arrays.stream( args ).filter( arg -> arg instanceof SshCredentials ).findFirst().orElse( null );
    if ( credentials != null && !( !( (SshCredentials) credentials ).getUsername().isEmpty()
      || !( (SshCredentials) credentials ).getIdentityPath().isEmpty() ) ) {
      SshCredentials sshCredentials = getCredentialsFromSecurityContext().getSshCredentials();
      ( (SshCredentials) credentials ).setUsername( sshCredentials.getUsername() );
      ( (SshCredentials) credentials ).setPassword( sshCredentials.getPassword() );
      ( (SshCredentials) credentials ).setIdentityPath( sshCredentials.getIdentityPath() );
    }

  }
}
