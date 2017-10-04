package com.epam.spring.search;

import com.epam.spring.condition.DownloadableFile;

import java.util.List;

public interface SearchStrategy {
    String getStrategyCommand();
    List<DownloadableFile> resolveCommandResult(String commandResult, List<DownloadableFile> searchableServiceNames);
}
