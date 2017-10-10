package com.epam.spring.service.download;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.exception.ServiceException;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.service.FileExtractingService;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
public class HttpDownloadService {
  @Autowired
  private FileExtractingService fileExtractingService;

  @SecurityAnnotation
  public CompletableFuture<Boolean> loadConfigsFromUri( String uri, DownloadPlan.LoadPathConfig loadPathConfig,
                                                        ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> {
      HttpResponse clientConfigsResponse = askForClientsConfigs( uri );

      return clientConfigsResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
        && saveClientsConfigs( extractByteArrayFromResponse( clientConfigsResponse ), loadPathConfig );
    }, executorService );
  }

  private HttpResponse askForClientsConfigs( String uri ) {
    try {
      return CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
        .execute( CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest( uri ) );
    } catch ( IOException | CommonUtilException e ) {
      throw new ServiceException( e );
    }
  }

  private boolean saveClientsConfigs( byte[] configsArray, DownloadPlan.LoadPathConfig loadPathConfig ) {
    fileExtractingService.getExtractFunction( loadPathConfig.getExtractFormat() )
      .accept( configsArray, loadPathConfig );

    return true;
  }

  private byte[] extractByteArrayFromResponse( HttpResponse clientConfigsResponse ) {
    try {
      return IOUtils.toByteArray( clientConfigsResponse.getEntity().getContent() );
    } catch ( IOException e ) {
      throw new ServiceException( e );
    }
  }
}
