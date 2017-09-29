package com.epam.spring.aspect;

import com.epam.kerberos.HadoopKerberosUtil;
import com.epam.spring.authenticate.impl.TestConfigLoadCredentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.stereotype.Component;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

@Aspect
@Component
public class TestAspect {
    private String testString = "test";
    //@Before("execution(* com.epam.spring.component.SpringComponent.sendMessage(..))")
    public void logBefore(JoinPoint joinPoint) {
        KerberosRestTemplate restTemplate =
                new KerberosRestTemplate(null, "mapr@PENTAHOQA.COM");
        restTemplate.getForObject("https://svqxbdcn6mapr52secn1.pentahoqa.com:8443/rest/service/list", String.class);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        System.out.println("logBefore() is running!");
        System.out.println("hijacked : " + username);
        System.out.println("******");
    }

    @Pointcut("@annotation(com.epam.spring.annotation.SecurityAnnotation)")
    public void test(){

    }

    @Around("test()")
    public boolean aroundTest(ProceedingJoinPoint joinPoint) throws Exception {
        handleAuthentication();

        return (Boolean) Subject.doAs(getKrb5Subject(), (PrivilegedExceptionAction<Object>) () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw (Exception) throwable;
            }
        });
    }

    private void handleAuthentication() throws Exception{
        Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (loggedAuthentication instanceof TestConfigLoadCredentials){
            TestConfigLoadCredentials authentication = (TestConfigLoadCredentials) loggedAuthentication;
            if (authentication.getKrb5Subject() == null){
                authentication.setKrbSubject(HadoopKerberosUtil.doLogin("devuser", "password").getSubject());
                //SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            //if (authentication.getHttpClient() == null) {
                HttpClientBuilder builder = HttpClientBuilder.create();

                SSLContextBuilder sslcb = new SSLContextBuilder();
                sslcb.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()),
                        new TrustSelfSignedStrategy());
                builder.setSSLContext(sslcb.build());

//            Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
//                    register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
//             builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
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
                builder.setDefaultCredentialsProvider(credentialsProvider);

                authentication.setHttpClient(builder.build());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            //}
        }
    }

    private Subject getKrb5Subject() throws Exception{
        Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (loggedAuthentication instanceof TestConfigLoadCredentials){
            TestConfigLoadCredentials authentication = (TestConfigLoadCredentials) loggedAuthentication;
            if (authentication.getKrb5Subject() != null){
                return authentication.getKrb5Subject();
            }
        }

        throw new Exception("tt");
    }
}
