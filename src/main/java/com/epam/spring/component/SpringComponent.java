package com.epam.spring.component;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.authenticate.impl.TestConfigLoadCredentials;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;

@Component
//@Scope("singleton")
public class SpringComponent {
    @Autowired
    private SpringService springService;

    @SecurityAnnotation
    public boolean sendMessage(String message, boolean bool) throws Exception{
        System.out.println("start");
        Date date = new Date();
        long start = date.getTime();
        CloseableHttpClient httpClient = getHttpClient();
        date = new Date();
        System.out.println(start - date.getTime());
        ArrayList<String> authPrefs = new ArrayList<String>();
        authPrefs.add(AuthSchemes.BASIC);
        authPrefs.add(AuthSchemes.SPNEGO);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials( "admin", "admin" );
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        credentialsProvider.setCredentials(new AuthScope(null, -1, AuthScope.ANY_REALM, AuthPolicy.SPNEGO), new Credentials() {
            @Override
            public Principal getUserPrincipal() {
                return null;
            }
            @Override
            public String getPassword() {
                return null;
            }
        });

        RequestBuilder rb = RequestBuilder.get().setUri( "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HIVE/components" );
        RequestConfig config = RequestConfig.custom()
                .setTargetPreferredAuthSchemes(authPrefs).build();
        rb.setConfig(config);

        HttpUriRequest request = rb.build();
        request.setHeader((new BasicScheme().authenticate(credentials, request, null)));
        HttpResponse httpResponse = httpClient.execute( request );
        date = new Date();
        System.out.println(start - date.getTime());
        return springService.sendMessage(message);
    }

    private CloseableHttpClient getHttpClient() throws Exception{
        Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (loggedAuthentication instanceof TestConfigLoadCredentials){
            TestConfigLoadCredentials authentication = (TestConfigLoadCredentials) loggedAuthentication;
            if (authentication.getHttpClient() != null){
                return authentication.getHttpClient();
            }
        }

        throw new Exception("tt");
    }

    public void testing(String string){
        System.out.println(string);
    }
}
