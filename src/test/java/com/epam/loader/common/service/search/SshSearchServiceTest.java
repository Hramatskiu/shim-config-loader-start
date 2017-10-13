package com.epam.loader.common.service.search;

import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.exception.ServiceException;
import com.epam.loader.plan.strategy.SearchStrategy;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.common.util.SshCommonUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

@RunWith( PowerMockRunner.class )
@PrepareForTest( CommonUtilHolder.class )
public class SshSearchServiceTest {

  @SuppressWarnings( "unchecked" )
  @Test( expected = ServiceException.class )
  public void searchForConfigsLocationWhenCommonUtilRaiseExceptionShouldRaiseServiceException() throws Exception {
    SshSearchService sshSearchService = new SshSearchService();
    SshCommonUtil sshCommonUtil = Mockito.mock( SshCommonUtil.class );
    SearchStrategy searchStrategy = Mockito.mock( SearchStrategy.class );
    PowerMockito.mockStatic( CommonUtilHolder.class );

    Mockito.when( CommonUtilHolder.sshCommonUtilInstance() ).thenReturn( sshCommonUtil );
    Mockito.when( sshCommonUtil.executeCommand( Mockito.any( SshCredentials.class ),
      Mockito.anyString(), Mockito.anyInt(),  Mockito.anyString() ) )
      .thenThrow( CommonUtilException.class );
    Mockito.when( searchStrategy.getStrategyCommand( Mockito.anyList() ) ).thenReturn( "some" );

    sshSearchService.searchForConfigsLocation( "some", Collections.emptyList(), searchStrategy );
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void searchForConfigsLocationWhenCommonUtilExecuteCommandSuccessfullyShouldReturnNotNullList()
    throws Exception {
    SshSearchService sshSearchService = new SshSearchService();
    SshCommonUtil sshCommonUtil = Mockito.mock( SshCommonUtil.class );
    SearchStrategy searchStrategy = Mockito.mock( SearchStrategy.class );
    PowerMockito.mockStatic( CommonUtilHolder.class );

    Mockito.when( CommonUtilHolder.sshCommonUtilInstance() ).thenReturn( sshCommonUtil );
    Mockito.when( sshCommonUtil.executeCommand( Mockito.any( SshCredentials.class ),
      Mockito.anyString(), Mockito.anyInt(), Mockito.anyString() ) )
      .thenReturn( "test" );
    Mockito.when( searchStrategy.getStrategyCommand( Mockito.anyList() ) ).thenReturn( StringUtils.EMPTY );
    Mockito.when( searchStrategy.resolveCommandResult( Mockito.anyString(), Mockito.anyList() ) )
      .thenReturn( Collections.singletonList( new DownloadableFile( StringUtils.EMPTY, Collections.emptyList() ) ) );

    Assert.assertNotNull(
      sshSearchService.searchForConfigsLocation( StringUtils.EMPTY, Collections.emptyList(), searchStrategy ) );
  }

  @Test
  public void askForClientsConfigLocationWhenCommandIsNullShouldReturnEmptyString() throws Exception {
    SshSearchService sshSearchService = new SshSearchService();
    Assert.assertTrue( sshSearchService.askForClientsConfigLocation( StringUtils.EMPTY, null ).isEmpty() );
  }


}