package com.epam.spring.service;

import com.epam.spring.util.FileCommonUtil;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith( PowerMockRunner.class )
@PrepareForTest( FileCommonUtil.class )
public class FileExtractingServiceTest {
  //  @SuppressWarnings( "unchecked" ) @Test
  //  public void testGetExtractFunctionWhenKeyIsTARShouldReturnTarFunction() throws CommonUtilException {
  //    PowerMockito.mockStatic( FileCommonUtil.class );
  //    FileExtractingService fileExtractingService = new FileExtractingService();
  //
  //    Mockito.when( FileCommonUtil.extractFilesFromTarArchiveByteArray( Mockito.any(), Mockito.anyList(), Mockito
  // .anyString() ) ).thenThrow(
  //      CommonUtilException.class );
  //
  //  }

}