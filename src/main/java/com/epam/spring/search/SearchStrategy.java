package com.epam.spring.search;

import java.util.List;

public interface SearchStrategy {
    String getStrategyCommand();
    List<String> resolveCommandResult(String commandResult, List<String> searchableServiceNames);
}
