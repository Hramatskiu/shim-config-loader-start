package com.epam.spring.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.exception.StrategyException;
import com.epam.spring.util.CheckingParamsUtil;
import org.apache.log4j.Logger;

import java.util.List;

public interface SearchStrategy {
  Logger logger = Logger.getLogger( SearchStrategy.class );

  String getStrategyCommand( List<DownloadableFile> searchableServiceNames );

  List<DownloadableFile> resolveCommandResult( String commandResult, List<DownloadableFile> searchableServiceNames )
    throws
    StrategyException;

  default List<DownloadableFile> tryToResolveCommandResult( String commandResult,
                                                            List<DownloadableFile> searchableServiceNames )
    throws StrategyException {
    validateCommandResult( commandResult );

    return resolveCommandResult( commandResult, searchableServiceNames );
  }

  default void validateCommandResult( String commandResult ) throws StrategyException {
    if ( !CheckingParamsUtil.checkParamsWithNullAndEmpty( commandResult ) ) {
      throw new StrategyException( "Invalid command result." );
    }
  }
}
