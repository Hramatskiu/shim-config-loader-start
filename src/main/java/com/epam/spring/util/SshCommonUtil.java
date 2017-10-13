package com.epam.spring.util;

import com.epam.spring.config.SshCredentials;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.ssh.DelegatingSshSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SshCommonUtil {
  public String executeCommand( SshCredentials sshCredentials, String host, int port, String command ) throws
    CommonUtilException {
    try ( DelegatingSshSession sshSession = new DelegatingSshSession( sshCredentials.getUsername(), host, port,
      sshCredentials.getPassword(), sshCredentials.getIdentityPath() ) ) {
      return sshSession.executeCommand( command );
    } catch ( IOException ex ) {
      throw new CommonUtilException( ex );
    }
  }

  public String downloadConfigs( SshCredentials sshCredentials, String host, int port, String source ) throws
    CommonUtilException {
    try ( DelegatingSshSession sshSession = new DelegatingSshSession( sshCredentials.getUsername(), host, port,
      sshCredentials.getPassword(), sshCredentials.getIdentityPath() ) ) {
      return sshSession.downloadFile( source );
    } catch ( IOException ex ) {
      throw new CommonUtilException( ex );
    }
  }
}
