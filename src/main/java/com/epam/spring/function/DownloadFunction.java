package com.epam.spring.function;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.search.SearchStrategy;

public abstract class DownloadFunction {
    public abstract void downloadConfigs(String remoteUrl, DownloadConfigsCondition downloadConfigsCondition, String format, SearchStrategy searchStrategy) throws Exception;
}
