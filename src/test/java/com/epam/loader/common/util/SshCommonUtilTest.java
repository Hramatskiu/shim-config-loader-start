package com.epam.loader.common.util;

import com.epam.loader.config.credentials.SshCredentials;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class SshCommonUtilTest {
  @SuppressWarnings( "unchecked" )
  @Test(expected = CommonUtilException.class)
  public void executeCommandWhenSessionCannotBeCreatedShouldRaiseCommonUtilException() throws Exception {
    SshCommonUtil sshCommonUtil = Mockito.mock( SshCommonUtil.class );

    Mockito.when( sshCommonUtil.createDelegationSshSession( Mockito.any( SshCredentials.class ), Mockito.anyString(), Mockito.anyInt() ) )
      .thenThrow( IOException.class );
    Mockito.when( sshCommonUtil.executeCommand( Mockito.any( SshCredentials.class ), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString() ) )
      .thenCallRealMethod();

    sshCommonUtil.executeCommand( new SshCredentials(  ), StringUtils.EMPTY, 0, StringUtils.EMPTY );
  }

  @SuppressWarnings( "unchecked" )
  @Test(expected = CommonUtilException.class)
  public void downloadViaSftpWhenSessionCannotBeCreatedShouldRaiseCommonUtilException() throws Exception {
    SshCommonUtil sshCommonUtil = Mockito.mock( SshCommonUtil.class );

    Mockito.when( sshCommonUtil.createDelegationSshSession( Mockito.any( SshCredentials.class ), Mockito.anyString(), Mockito.anyInt() ) )
      .thenThrow( IOException.class );
    Mockito.when( sshCommonUtil.downloadViaSftp( Mockito.any( SshCredentials.class ), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString() ) )
      .thenCallRealMethod();

    sshCommonUtil.downloadViaSftp( new SshCredentials(  ), StringUtils.EMPTY, 0, StringUtils.EMPTY );
  }


}