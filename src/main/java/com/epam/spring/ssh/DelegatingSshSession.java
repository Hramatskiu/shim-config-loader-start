package com.epam.spring.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class DelegatingSshSession implements Closeable {
  private Session session;

  public DelegatingSshSession( String user, String host, int port, String password ) {
    this.session = createSession( user, host, port, password );
  }

  public String executeCommand( String command ) {
    StringBuilder commandResult = new StringBuilder( StringUtils.EMPTY );
    Channel channel = null;

    try {
      channel = session.openChannel( "exec" );
      ( (ChannelExec) channel ).setCommand( command );

      channel.setInputStream( null );
      ( (ChannelExec) channel ).setErrStream( System.err );
      InputStream in = channel.getInputStream();
      channel.connect();
      byte[] tmp = new byte[ 1024 ];
      while ( true ) {
        while ( in.available() > 0 ) {
          int i = in.read( tmp, 0, 1024 );
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
          Thread.sleep( 1000 );
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

  private Session createSession( String user, String host, int port, String password ) {
    try {
      JSch jsch = new JSch();
      Session session = jsch.getSession( user, host, 22 );
      session.setConfig( "StrictHostKeyChecking", "no" );
      session.setPassword( password );
      session.connect();

      return session;
    } catch ( JSchException ex ) {
      ex.printStackTrace();
    }

    return null;
  }
}
