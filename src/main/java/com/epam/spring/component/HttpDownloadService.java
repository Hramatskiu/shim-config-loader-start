package com.epam.spring.component;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.util.CommonUtilHolder;
import com.epam.spring.util.FileCommonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Component
//@Scope("singleton")
public class HttpDownloadService {
    @SecurityAnnotation
    public CompletableFuture<Boolean> loadConfigsFromUri(String uri, String dest) throws Exception{
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

    //Think about
    private boolean saveClientsConfigs(byte[] configsArray, String dest) {
        FileCommonUtil.writeByteArrayToFile(dest, configsArray);

        return true;
    }
}
