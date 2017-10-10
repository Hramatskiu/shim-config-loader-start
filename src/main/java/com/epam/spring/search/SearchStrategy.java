package com.epam.spring.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.exception.StrategyException;

import java.util.List;

public interface SearchStrategy {
  String getStrategyCommand();

  List<DownloadableFile> resolveCommandResult( String commandResult, List<DownloadableFile> searchableServiceNames )
    throws
    StrategyException;
}
