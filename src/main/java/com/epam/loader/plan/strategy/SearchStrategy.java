package com.epam.loader.plan.strategy;

import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.config.condition.DownloadableFile;
import org.apache.log4j.Logger;

import java.util.List;

public interface SearchStrategy {
  Logger logger = Logger.getLogger( SearchStrategy.class );

  String getStrategyCommand( List<DownloadableFile> searchableServiceNames );

  List<DownloadableFile> resolveCommandResult( String commandResult, List<DownloadableFile> searchableServiceNames )
    throws StrategyException;

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

  default boolean useSsh() {
    return false;
  }
}
