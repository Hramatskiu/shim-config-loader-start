package com.epam.spring.service.download;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.util.FileCommonUtil;
import com.epam.spring.util.SshCommonUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Component
public class SshDownloadService {
  @SecurityAnnotation
  public CompletableFuture<Boolean> loadConfigsFromCommand( String host, String command,
                                                            DownloadPlan.LoadPathConfig loadPathConfig,
                                                            ExecutorService executorService ) throws Exception {
    return CompletableFuture.supplyAsync( () -> {
      try {
        List<String> answer = new ArrayList<>();
        loadPathConfig.getLoadedFiles().forEach( file -> {
          try {
            answer.add( askForClientsConfigs( host, "cat " + command + file ) );
          } catch ( Exception e ) {
            e.printStackTrace();
          }
        } );

        return checkAnswer( answer, loadPathConfig.getLoadedFiles() ) && saveClientsConfigs( answer, loadPathConfig );
      } catch ( Exception e ) {
        throw new CompletionException( e );
      }
    }, executorService );
  }

  private String askForClientsConfigs( String host, String command ) throws Exception {
    return SshCommonUtil.executeCommand( "mapr", "password", host, 22, command );
  }

  private boolean checkAnswer( List<String> answer, List<String> loaddedFileNames ) {
    return !answer.isEmpty() && answer.size() == loaddedFileNames.size();
  }

  //Think about
  private boolean saveClientsConfigs( List<String> configString, DownloadPlan.LoadPathConfig loadPathConfig )
    throws Exception {
    Iterator<String> iterator = configString.iterator();
    loadPathConfig.getLoadedFiles().forEach( file -> {
      if ( iterator.hasNext() ) {
        FileCommonUtil.writeStringToFile( loadPathConfig.getDestPrefix() + "\\" + file, iterator.next() );
      }
    } );

    return true;
  }
}
