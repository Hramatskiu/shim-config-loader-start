package com.epam.spring.security;

import com.epam.kerberos.HadoopKerberosUtil;
import com.epam.spring.authenticate.impl.BaseConfigLoadAuthentication;
import com.epam.spring.authenticate.impl.TestConfigLoadCredentials;
import com.epam.spring.config.HttpCredentials;
import com.epam.spring.config.Krb5Credentials;
import com.epam.spring.config.SshCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class AutheticationManagerImpl implements AuthenticationManager {
  public Authentication authenticate( Authentication auth ) throws AuthenticationException {
    if ( auth instanceof TestConfigLoadCredentials && auth.isAuthenticated() ) {
      return auth;
    }

    if ( auth instanceof BaseConfigLoadAuthentication ) {
      return makeAuthentication( (BaseConfigLoadAuthentication) auth );
    }

    //change on own exception extends AuthenticationException
    throw new BadCredentialsException( "Can't authenticate" );
  }

  private Authentication makeAuthentication( BaseConfigLoadAuthentication authentication )
    throws AuthenticationException {
    TestConfigLoadCredentials testConfigLoadCredentials = new TestConfigLoadCredentials();

    loginWithKerberos( authentication.getKrb5Credentials() );

    testConfigLoadCredentials
      .setCredentialsProvider( createHttpCredentialsProvider( authentication.getHttpCredentials() ) );
    testConfigLoadCredentials.setAuthShemes( createAuthShemesList() );
    //testConfigLoadCredentials.setKrbSubject(createKrb5Subject(authentication.getKrb5Credentials()));
    testConfigLoadCredentials.setSshCredentials( createSshCredentials( authentication.getSshCredentials() ) );

    return testConfigLoadCredentials;
  }

  private CredentialsProvider createHttpCredentialsProvider( HttpCredentials httpCredentials )
    throws AuthenticationException {
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials =
      new UsernamePasswordCredentials( httpCredentials.getUsername(), httpCredentials.getPassword() );
    credentialsProvider.setCredentials( AuthScope.ANY, credentials );
    credentialsProvider
      .setCredentials( new AuthScope( null, -1, AuthScope.ANY_REALM, AuthPolicy.SPNEGO ), new Credentials() {
        @Override
        public Principal getUserPrincipal() {
          return null;
        }

        @Override
        public String getPassword() {
          return null;
        }
      } );

    return credentialsProvider;
  }

  private List<String> createAuthShemesList() {
    List<String> authShemes = new ArrayList<>();

    authShemes.add( AuthSchemes.BASIC );
    authShemes.add( AuthSchemes.SPNEGO );

    return authShemes;
  }

  private void loginWithKerberos( Krb5Credentials krb5Credentials ) throws AuthenticationException {
    try {
      if ( krb5Credentials.getKeytabLocation() != null && !krb5Credentials.getKeytabLocation().isEmpty() ) {
        HadoopKerberosUtil.doLoginWithKeytab( krb5Credentials.getUsername(), krb5Credentials.getKeytabLocation() );
      } else {
        HadoopKerberosUtil
          .doLoginWithPrincipalAndPassword( krb5Credentials.getUsername(), krb5Credentials.getPassword() );
      }
    } catch ( IOException | LoginException ex ) {
      throw new BadCredentialsException( "Bad kerberos credentials", ex );
    }
  }

  //Think about
  private SshCredentials createSshCredentials( SshCredentials sshCredentials ) throws AuthenticationException {
    return sshCredentials;
  }
}
