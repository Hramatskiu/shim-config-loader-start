package com.epam.spring.util;

import com.epam.spring.ssh.DelegatingSshSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SshCommonUtil {
  public String executeCommand( String username, String password, String host, int port, String command ) {
    String commandResult = StringUtils.EMPTY;
    try ( DelegatingSshSession sshSession = new DelegatingSshSession( username, host, port, password ) ) {
      commandResult = sshSession.executeCommand( command );
    } catch ( IOException ex ) {
      //do smth
    }

    return commandResult;
  }
}
