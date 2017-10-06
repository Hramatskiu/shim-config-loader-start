package com.epam.spring.function.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.service.download.HttpDownloadService;
import com.epam.spring.service.search.HttpSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class HttpDownloadFunction extends DownloadFunction {
    @Autowired
    private HttpDownloadService downloadService;
    @Autowired
    private HttpSearchService httpSearchService;

    public void downloadConfigs(DownloadConfigsCondition downloadConfigsCondition, SearchStrategy searchStrategy,
                                DownloadPlan.LoadPathConfig loadPathConfig) throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<CompletableFuture<Boolean>> taskList = httpSearchService.searchForConfigsLocation(loadPathConfig.getCompositeHost(),
                downloadConfigsCondition.getUnloadedConfigsList(), searchStrategy).stream()
                .map(file -> {
                    try {
                        DownloadPlan.LoadPathConfig copiedLoadPathConfig = copyLoadPathConfig(loadPathConfig);
                        copiedLoadPathConfig.setLoadedFiles(file.getFiles());

                        return downloadService.loadConfigsFromUri(file.getDownloadPath(), copiedLoadPathConfig, executor);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }).collect(Collectors.toList());

        downloadConfigsCondition.setDownloadCondition(
                taskList.stream().map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        executor.shutdown();
    }

    private DownloadPlan.LoadPathConfig copyLoadPathConfig(DownloadPlan.LoadPathConfig loadPathConfig) {
        return new DownloadPlan.LoadPathConfig(loadPathConfig.getCompositeHost(),
                loadPathConfig.getDestPrefix(), loadPathConfig.getExtractFormat());
    }

}
