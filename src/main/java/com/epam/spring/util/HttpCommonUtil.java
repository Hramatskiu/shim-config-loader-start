package com.epam.spring.util;

import com.epam.spring.exception.CommonUtilException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Component
public class HttpCommonUtil {
  public HttpClientBuilder createHttpClientBuilder() {
    return HttpClientBuilder.create();
  }

  public RequestBuilder createRequestBuilder( String uri ) {
    return RequestBuilder.get().setUri( uri );
  }

  public CloseableHttpClient createHttpClient() throws CommonUtilException {
    HttpClientBuilder builder = CommonUtilHolder.httpCommonUtilInstance().createHttpClientBuilder();
    builder.setSSLContext( createSslContext() );

    return builder.build();
  }

  public HttpUriRequest createHttpUriRequest( String uri ) {
    return CommonUtilHolder.httpCommonUtilInstance().createRequestBuilder( uri ).build();
  }

  private SSLContext createSslContext() throws CommonUtilException {
    try {
      SSLContextBuilder sslcb = new SSLContextBuilder();
      sslcb.loadTrustMaterial( KeyStore.getInstance( KeyStore.getDefaultType() ),
        new TrustSelfSignedStrategy() );

      return sslcb.build();
    } catch ( NoSuchAlgorithmException | KeyStoreException | KeyManagementException e ) {
      throw new CommonUtilException( e );
    }
  }
}
