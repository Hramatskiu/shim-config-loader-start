package com.epam.spring.aspect;

import com.epam.spring.security.BaseSecurityContextHandler;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class HttpSecurityAspect extends BaseSecurityContextHandler {
  @Pointcut( "execution(* com.epam.spring.util.HttpCommonUtil.createHttpClientBuilder())" )
  public void addHttpClientBuilderSecurity() {
  }

  @Pointcut( "execution(* com.epam.spring.util.HttpCommonUtil.createRequestBuilder(..))" )
  public void addHttpRequestBuilderSecurity() {
  }

  @Pointcut( "execution(* com.epam.spring.util.HttpCommonUtil.createHttpUriRequest(..))" )
  public void addHttpRequestSecurity() {
  }

  @Around( "addHttpClientBuilderSecurity()" )
  public HttpClientBuilder setHttpSecurityToClientBuilder( ProceedingJoinPoint joinPoint ) throws Throwable {
    Object httpClientBuilder = joinPoint.proceed();
    ( (HttpClientBuilder) httpClientBuilder )
      .setDefaultCredentialsProvider( getCredentialsFromSecurityContext().getCredentialsProvider() );

    return (HttpClientBuilder) httpClientBuilder;
  }

  @Around( "addHttpRequestBuilderSecurity()" )
  public RequestBuilder setHttpSecurityToRequestBuilder( ProceedingJoinPoint joinPoint ) throws Throwable {
    Object requestBuilder = joinPoint.proceed();
    ( (RequestBuilder) requestBuilder ).setConfig( RequestConfig.custom()
      .setTargetPreferredAuthSchemes( getPrefShemesList() ).build() );

    return (RequestBuilder) requestBuilder;
  }

  @Around( "addHttpRequestSecurity()" )
  public HttpUriRequest setHttpSecurityToRequest( ProceedingJoinPoint joinPoint ) throws Throwable {
    Object request = joinPoint.proceed();
    ( (HttpUriRequest) request ).setHeader( (
      new BasicScheme()
        .authenticate( getCredentialsFromSecurityContext().getCredentialsProvider().getCredentials( AuthScope.ANY ),
          (HttpUriRequest) request, null ) ) );

    return (HttpUriRequest) request;
  }

  private List<String> getPrefShemesList() throws Exception {
    return getCredentialsFromSecurityContext().getAuthShemes();
  }
}
