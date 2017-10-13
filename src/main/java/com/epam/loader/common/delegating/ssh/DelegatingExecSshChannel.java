package com.epam.loader.common.delegating.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.apache.commons.lang.StringUtils;

import java.io.Closeable;
import java.io.IOException;

public class DelegatingExecSshChannel implements Closeable {
  private ChannelExec channelExec;

  public DelegatingExecSshChannel( Session session ) throws Exception {
    this.channelExec = (ChannelExec) session.openChannel( "exec" );
  }

  public String executeCommand( String command ) {
    return StringUtils.EMPTY;
  }

  @Override public void close() throws IOException {
    if ( !channelExec.isConnected() ) {
      channelExec.disconnect();
    }
  }
}
