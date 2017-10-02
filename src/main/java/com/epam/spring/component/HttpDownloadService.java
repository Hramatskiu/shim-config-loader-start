package com.epam.spring.component;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.util.CommonUtilHolder;
import com.epam.spring.util.FileCommonUtil;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
//@Scope("singleton")
public class HttpDownloadService {
    @SecurityAnnotation
    public boolean loadConfigsFromUri(String uri) throws Exception{
        CloseableHttpClient httpClient = CommonUtilHolder.httpCommonUtilInstance().createHttpClient();

        HttpResponse httpResponse = httpClient.execute(
                CommonUtilHolder.httpCommonUtilInstance()
                        .createHttpUriRequest(uri) );
        byte[] bytes = IOUtils.toByteArray (httpResponse.getEntity().getContent());
        FileCommonUtil.writeByteArrayToFile("test.tar", bytes);
        System.out.println("Status: " + httpResponse.getStatusLine().getStatusCode());

        return true;
    }
}
