package com.epam.loader.common.util;

import com.epam.loader.common.delegating.ssh.DelegatingSshSession;
import com.epam.loader.common.holder.DownloadedFileWrapper;
import com.epam.loader.config.credentials.SshCredentials;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SshCommonUtil {
  public String executeCommand( SshCredentials sshCredentials, String host, int port, String command ) throws
    CommonUtilException {
    try ( DelegatingSshSession sshSession = createDelegationSshSession( sshCredentials, host, port ) ) {
      return sshSession.executeCommand( command );
    } catch ( IOException ex ) {
      throw new CommonUtilException( ex );
    }
  }

  public String downloadViaSftp( SshCredentials sshCredentials, String host, int port, String source ) throws
    CommonUtilException {
    try ( DelegatingSshSession sshSession = createDelegationSshSession( sshCredentials, host, port ) ) {
      return sshSession.downloadFile( source );
    } catch ( IOException ex ) {
      throw new CommonUtilException( ex );
    }
  }

  public DownloadedFileWrapper downloadViaSftpAsFileWrapper( SshCredentials sshCredentials, String host, int port,
                                                             String source ) throws CommonUtilException {
    try ( DelegatingSshSession sshSession = createDelegationSshSession( sshCredentials, host, port ) ) {
      return new DownloadedFileWrapper( sshSession.downloadFileAsByteArray( source ) );
    } catch ( IOException ex ) {
      throw new CommonUtilException( ex );
    }
  }

  DelegatingSshSession createDelegationSshSession( SshCredentials sshCredentials, String host, int port )
    throws IOException {
    return new DelegatingSshSession( sshCredentials.getUsername(), host, port,
      sshCredentials.getPassword(), sshCredentials.getIdentityPath() );
  }
}
