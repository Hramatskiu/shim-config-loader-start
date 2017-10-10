package com.epam.spring.util;

import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.ssh.DelegatingSshSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SshCommonUtil {
  public String executeCommand( String username, String password, String host, int port, String command ) throws
    CommonUtilException {
    try ( DelegatingSshSession sshSession = new DelegatingSshSession( username, host, port, password ) ) {
      return sshSession.executeCommand( command );
    } catch ( IOException ex ) {
      throw new CommonUtilException( ex );
    }
  }
}
