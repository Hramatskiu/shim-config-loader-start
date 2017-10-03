package com.epam.spring.function.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.service.download.HttpDownloadService;
import com.epam.spring.service.search.HttpSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class HttpDownloadFunction extends DownloadFunction {
    @Autowired
    private HttpDownloadService downloadService;
    @Autowired
    private HttpSearchService httpSearchService;

    public void downloadConfigs(String remoteUrl, DownloadConfigsCondition downloadConfigsCondition) throws Exception{
        List<String> uris = httpSearchService.searchForConfigsLocation(remoteUrl, downloadConfigsCondition.getUnloadedConfigsList());
        List<String> dests = downloadConfigsCondition.getUnloadedConfigsList().stream().map(dest -> dest + "-site.xml").collect(Collectors.toList());

        List<CompletableFuture<Boolean>> taskList = uris.stream().map(uri -> {
            try {
                return downloadService.loadConfigsFromUri(uri, dests);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new CompletableFuture<Boolean>();
        }).collect(Collectors.toList());

        List<Boolean> results = taskList.stream().map(CompletableFuture::join).collect(Collectors.toList());
        results.forEach(System.out::println);
    }
}
