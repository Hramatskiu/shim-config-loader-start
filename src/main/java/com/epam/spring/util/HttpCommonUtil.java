package com.epam.spring.util;

import org.apache.http.auth.AuthScope;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.security.KeyStore;

@Component
public class HttpCommonUtil {
    public HttpClientBuilder createHttpClientBuilder() {
        return HttpClientBuilder.create();
    }

    public RequestBuilder createRequestBuilder(String uri) throws Exception{
        return RequestBuilder.get().setUri( uri );
    }

    public CloseableHttpClient createHttpClient() throws Exception{
        HttpClientBuilder builder = CommonUtilHolder.httpCommonUtilInstance().createHttpClientBuilder();

        SSLContextBuilder sslcb = new SSLContextBuilder();
        sslcb.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()),
                new TrustSelfSignedStrategy());
        builder.setSSLContext(sslcb.build());

        return builder.build();
    }

    public HttpUriRequest createHttpUriRequest(String uri) throws Exception {
        return CommonUtilHolder.httpCommonUtilInstance().createRequestBuilder(uri).build();
    }
}
