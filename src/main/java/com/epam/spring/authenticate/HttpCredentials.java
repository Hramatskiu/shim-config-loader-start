package com.epam.spring.authenticate;

import org.apache.http.impl.client.CloseableHttpClient;

public interface HttpCredentials {
    CloseableHttpClient getHttpClient();
    void setHttpClient(CloseableHttpClient httpClient);
}
