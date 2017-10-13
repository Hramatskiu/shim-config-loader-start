package com.epam.spring.security;

import com.epam.kerberos.HadoopKerberosUtil;
import com.epam.loader.config.credentials.HttpCredentials;
import com.epam.loader.config.credentials.Krb5Credentials;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.spring.security.authenticate.impl.BaseConfigLoadAuthentication;
import com.epam.spring.security.authenticate.impl.ConfigLoadCredentials;
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
    if ( auth instanceof ConfigLoadCredentials && auth.isAuthenticated() ) {
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
    ConfigLoadCredentials configLoadCredentials = new ConfigLoadCredentials();

    if ( !authentication.getKrb5Credentials().getUsername().isEmpty() ) {
      loginWithKerberos( authentication.getKrb5Credentials() );
    }

    configLoadCredentials
      .setCredentialsProvider( createHttpCredentialsProvider( authentication.getHttpCredentials() ) );
    configLoadCredentials.setAuthShemes( createAuthShemesList() );
    configLoadCredentials.setSshCredentials( createSshCredentials( authentication.getSshCredentials() ) );
    configLoadCredentials.setKerberosAuth( !authentication.getKrb5Credentials().getUsername().isEmpty() );

    // Necessary?
    configLoadCredentials.setAuthenticated( true );

    return configLoadCredentials;
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
