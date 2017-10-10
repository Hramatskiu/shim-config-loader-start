package com.epam.spring.service.download;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.service.FileExtractingService;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Component
public class HttpDownloadService {
    @Autowired
    private FileExtractingService fileExtractingService;

    @SecurityAnnotation
    public CompletableFuture<Boolean> loadConfigsFromUri(String uri, DownloadPlan.LoadPathConfig loadPathConfig, ExecutorService executorService) throws Exception{
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse clientConfigsResponse = askForClientsConfigs(uri);

                return clientConfigsResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK &&
                        saveClientsConfigs(IOUtils.toByteArray(clientConfigsResponse.getEntity().getContent()), loadPathConfig);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private HttpResponse askForClientsConfigs(String uri) throws Exception{
        return CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
                .execute(CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest(uri));
    }

    //Think about
    private boolean saveClientsConfigs(byte[] configsArray, DownloadPlan.LoadPathConfig loadPathConfig) throws Exception{
        fileExtractingService.getExtractFunction(loadPathConfig.getExtractFormat()).accept(configsArray, loadPathConfig);

        return true;
    }
}
