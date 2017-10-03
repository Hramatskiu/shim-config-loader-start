package com.epam.spring.service.download;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.util.CommonUtilHolder;
import com.epam.spring.util.FileCommonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
//@Scope("singleton")
public class HttpDownloadService {
    @SecurityAnnotation
    public CompletableFuture<Boolean> loadConfigsFromUri(String uri, List<String> dest) throws Exception{
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse clientConfigsResponse = askForClientsConfigs(uri);

                return clientConfigsResponse.getStatusLine().getStatusCode() == 200 &&
                        saveClientsConfigs(IOUtils.toByteArray(clientConfigsResponse.getEntity().getContent()), dest);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    private HttpResponse askForClientsConfigs(String uri) throws Exception{
        return CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
                .execute(CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest(uri));
    }

    private boolean saveClientsConfigs(byte[] configsArray, List<String> dest) throws Exception{
        FileCommonUtil.extractFilesFromTarArchiveByteArray(configsArray, dest);

        return true;
    }
}
