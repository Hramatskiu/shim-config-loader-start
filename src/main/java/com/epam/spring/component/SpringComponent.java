package com.epam.spring.component;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@Scope("singleton")
public class SpringComponent {
    @Autowired
    private SpringService springService;

    @SecurityAnnotation
    public boolean sendMessage(String message, boolean bool) throws Exception{
        CloseableHttpClient httpClient = CommonUtilHolder.httpCommonUtilInstance().createHttpClient();

        HttpResponse httpResponse = httpClient.execute(
                CommonUtilHolder.httpCommonUtilInstance()
                        .createHttpUriRequest("http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HIVE/components") );
        System.out.println("Status: " + httpResponse.getStatusLine().getStatusCode());

        return springService.sendMessage(message);
    }
}
