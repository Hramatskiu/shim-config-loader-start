package com.epam.spring.search.impl;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.exception.StrategyException;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.CheckingParamsUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component( "cdh-rest-strategy" )
public class CdhSearchStrategy implements SearchStrategy {
  @Override
  public String getStrategyCommand( List<DownloadableFile> searchableServiceNames ) {
    return "clusters/";
  }

  @Override
  public List<DownloadableFile> resolveCommandResult( String commandResult,
                                                      List<DownloadableFile> searchableServiceNames )
    throws StrategyException {
    String clusterName = extractClusterNameFromCommandResult( commandResult );
    if ( !clusterName.isEmpty() ) {
      searchableServiceNames.forEach( service -> service
        .setDownloadPath( "clusters/" + clusterName + "/services/" + service.getServiceName() + "/clientConfig" ) );

      return searchableServiceNames;
    }

    return Collections.emptyList();
  }

  String extractClusterNameFromCommandResult( String commandResult ) throws StrategyException {
    try {
      return CheckingParamsUtil.checkParamsWithNullAndEmpty( commandResult )
        ? new JSONObject( commandResult ).getJSONArray( "items" ).getJSONObject( 0 ).getString( "name" )
        : StringUtils.EMPTY;
    } catch ( JSONException e ) {
      throw new StrategyException( e );
    }
  }
}
