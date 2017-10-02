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
    public boolean loadConfigsFromUri(String uri, String dest) throws Exception{
        CompletableFuture.supplyAsync(() -> {
            CloseableHttpClient httpClient = null;
            try {
                httpClient = CommonUtilHolder.httpCommonUtilInstance().createHttpClient();
                return IOUtils.toByteArray(httpClient.execute(CommonUtilHolder.httpCommonUtilInstance()
                        .createHttpUriRequest(uri) ).getEntity().getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }).thenAccept((array) -> FileCommonUtil.writeByteArrayToFile(dest, array)).join();

        //System.out.println("Status: " + httpResponse.getStatusLine().getStatusCode());

        return true;
    }
}
