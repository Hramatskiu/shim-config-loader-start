package com.epam.spring.aspect;

import com.epam.spring.authenticate.impl.TestConfigLoadCredentials;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class HttpSecurityAspect {
    @Pointcut("execution(* com.epam.spring.util.HttpCommonUtil.createHttpClientBuilder())")
    public void addHttpClientBuilderSecurity(){ }

    @Pointcut("execution(* com.epam.spring.util.HttpCommonUtil.createRequestBuilder(..))")
    public void addHttpRequestBuilderSecurity(){ }

    @Pointcut("execution(* com.epam.spring.util.HttpCommonUtil.createHttpUriRequest(..))")
    public void addHttpRequestSecurity(){ }

    @Around("addHttpClientBuilderSecurity()")
    public HttpClientBuilder setHttpSecurityToClientBuilder(ProceedingJoinPoint joinPoint) throws Exception{
        try {
            Object httpClientBuilder = joinPoint.proceed();
            ((HttpClientBuilder) httpClientBuilder).setDefaultCredentialsProvider(getCredentialsFromSecurityContext().getCredentialsProvider());

            return (HttpClientBuilder) httpClientBuilder;
        } catch (Throwable throwable) {
            throw (Exception) throwable;
        }
    }

    @Around("addHttpRequestBuilderSecurity()")
    public RequestBuilder setHttpSecurityToRequestBuilder(ProceedingJoinPoint joinPoint) throws Exception{
        try {
            Object requestBuilder = joinPoint.proceed();
            ((RequestBuilder) requestBuilder).setConfig(RequestConfig.custom()
                    .setTargetPreferredAuthSchemes(getPrefShemesList()).build());

            return (RequestBuilder) requestBuilder;
        } catch (Throwable throwable) {
            throw (Exception) throwable;
        }
    }

    @Around("addHttpRequestSecurity()")
    public HttpUriRequest setHttpSecurityToRequest(ProceedingJoinPoint joinPoint) throws Exception{
        try {
            Object request = joinPoint.proceed();
            ((HttpUriRequest) request).setHeader((
                    new BasicScheme().authenticate(getCredentialsFromSecurityContext().getCredentialsProvider().getCredentials(AuthScope.ANY), (HttpUriRequest) request, null)));

            return (HttpUriRequest) request;
        } catch (Throwable throwable) {
            throw (Exception) throwable;
        }
    }

    private List<String> getPrefShemesList() throws Exception {
        return getCredentialsFromSecurityContext().getAuthShemes();
    }

    private TestConfigLoadCredentials getCredentialsFromSecurityContext() throws Exception {
        Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (loggedAuthentication instanceof TestConfigLoadCredentials){
            return (TestConfigLoadCredentials) loggedAuthentication;
        }

        throw new Exception("tt");
    }
}
