package com.epam.loader.common.delegating.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class DelegatingSshSession implements Closeable {
  private Session session;

  public DelegatingSshSession( String user, String host, int port, String password, String identityPath )
    throws IOException {
    session = createSession( user, host, port, password, identityPath );
  }

  public String downloadFile( String sourcePath ) {
    StringBuilder commandResult = new StringBuilder( StringUtils.EMPTY );
    Channel channel = null;

    try {
      channel = session.openChannel( "sftp" );
      channel.connect();

      InputStream in = ( (ChannelSftp) channel ).get( sourcePath );
      byte[] tmp = new byte[ 10024 ];
      int i;
      while ( ( i = in.read( tmp, 0, 10024 ) ) > 0 ) {
        commandResult.append( new String( tmp, 0, i ) );
      }
    } catch ( JSchException | IOException | SftpException ex ) {
      ex.printStackTrace();
    } finally {
      if ( channel != null ) {
        channel.disconnect();
      }
    }

    return commandResult.toString();
  }

  public String executeCommand( String command ) {
    StringBuilder commandResult = new StringBuilder( StringUtils.EMPTY );
    Channel channel = null;

    try {
      channel = session.openChannel( "exec" );
      ( (ChannelExec) channel ).setCommand( command.trim() );

      channel.setInputStream( null );
      ( (ChannelExec) channel ).setErrStream( System.err );
      InputStream in = channel.getInputStream();
      channel.connect();
      byte[] tmp = new byte[ 10024 ];
      while ( true ) {
        while ( in.available() > 0 ) {
          int i = in.read( tmp, 0, 5024 );
          if ( i < 0 ) {
            break;
          }
          commandResult.append( new String( tmp, 0, i ) );
        }
        if ( channel.isClosed() ) {
          if ( in.available() > 0 ) {
            continue;
          }
          break;
        }
        try {
          Thread.sleep( 100 );
        } catch ( Exception ex ) {
          System.out.println( ex.getMessage() );
        }
      }
    } catch ( JSchException | IOException ex ) {
      ex.printStackTrace();
    } finally {
      if ( channel != null ) {
        channel.disconnect();
      }
    }

    return commandResult.toString();
  }

  @Override
  public void close() throws IOException {
    session.disconnect();
  }

  private Session createSession( String user, String host, int port, String password, String identityPath )
    throws IOException {
    try {
      JSch jsch = new JSch();
      if ( identityPath != null && !identityPath.isEmpty() ) {
        jsch.addIdentity( identityPath );
      }

      Session session = jsch.getSession( user, host, 22 );
      session.setConfig( "StrictHostKeyChecking", "no" );
      session.setConfig( "GSSAPIAuthentication", "yes" );
      session.setConfig( "GSSAPIDelegateCredentials", "no" );
      session.setConfig( "UseDNS", "no" );
      if ( identityPath == null || identityPath.isEmpty() ) {
        session.setPassword( password );
      }

      session.connect();

      return session;
    } catch ( JSchException ex ) {
      throw new IOException( ex );
    }
  }
}
