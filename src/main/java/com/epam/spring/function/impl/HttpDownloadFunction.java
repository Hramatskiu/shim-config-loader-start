package com.epam.spring.function.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.service.download.HttpDownloadService;
import com.epam.spring.service.search.HttpSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class HttpDownloadFunction extends DownloadFunction {
    @Autowired
    private HttpDownloadService downloadService;
    @Autowired
    private HttpSearchService httpSearchService;

    public void downloadConfigs(String remoteUrl, DownloadConfigsCondition downloadConfigsCondition, String format, SearchStrategy searchStrategy) throws Exception{
        List<DownloadableFile> uris = httpSearchService.searchForConfigsLocation(remoteUrl, downloadConfigsCondition.getUnloadedConfigsList(), searchStrategy);

        List<CompletableFuture<Boolean>> taskList = uris.stream().map(file -> {
            try {
                return downloadService.loadConfigsFromUri(file.getDownloadPath(), file.getFiles(), format);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new CompletableFuture<Boolean>();
        }).collect(Collectors.toList());

        //downloadConfigsCondition.getUnloadedConfigsList().stream().map(DownloadableFile::getServiceName).forEach(System.out::println);
        //downloadConfigsCondition.getUnloadedConfigsList().stream().map(DownloadableFile::getDownloadPath).forEach(System.out::println);
        List<Boolean> results = taskList.stream().map(CompletableFuture::join).collect(Collectors.toList());
        downloadConfigsCondition.setDownloadCondition(results);
        //results.forEach(System.out::println);
        //downloadConfigsCondition.getUnloadedConfigsList().stream().map(DownloadableFile::getServiceName).forEach(System.out::println);
    }
}
