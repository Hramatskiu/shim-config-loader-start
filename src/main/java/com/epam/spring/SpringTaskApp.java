package com.epam.spring;

import com.epam.kerberos.HadoopKerberosUtil;
import com.epam.spring.authenticate.impl.BaseConfigLoadAuthentication;
import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.function.impl.HttpDownloadFunction;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.plan.impl.CDHDownloadPlan;
import com.epam.spring.plan.impl.HDPDownloadPlan;
import com.epam.spring.service.download.HttpDownloadService;
import com.epam.spring.config.HttpCredentials;
import com.epam.spring.config.Krb5Credentials;
import com.epam.spring.config.SpringAppConfig;
import com.epam.spring.config.SshCredentials;
import com.epam.spring.security.AutheticationManagerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringTaskApp {
    public static void main(String[] args) throws Exception{
        Date date = new Date();
        long start = date.getTime();
        System.out.println(date.getTime());

        CompletableFuture<Void> loginKerberosTask = CompletableFuture.runAsync(() -> {
            try {
                Krb5Credentials krb5Credentials = new Krb5Credentials("devuser", "password", "PENTAHOQA.COM");
                HadoopKerberosUtil.doLogin(krb5Credentials.getUsername(), krb5Credentials.getPassword()).getSubject();
            } catch (LoginException | IOException e) {
                throw new CompletionException(e);
            }
        });

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringAppConfig.class);
        loginKerberosTask.join();

        configureSecurity(applicationContext);

        date = new Date();
        System.out.println(start - date.getTime());

        Stream.of(CompletableFuture.supplyAsync(() -> {
            try {
                return applicationContext.getBean(HDPDownloadPlan.class).downloadConfigs("svqxbdcn6hdp26secn1.pentahoqa.com", "D:\\test_conf_hdp");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }), CompletableFuture.supplyAsync(() -> {
            try {
                return applicationContext.getBean(CDHDownloadPlan.class).downloadConfigs("svqxbdcn6cdh512secn1.pentahoqa.com", "D:\\test_conf_cdh");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        })).map(CompletableFuture::join).collect(Collectors.toList()).forEach(System.out::println);

        date = new Date();
        System.out.println(start - date.getTime());
    }

    private static void configureSecurity(ApplicationContext applicationContext) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.getContext()
                .setAuthentication(applicationContext.getBean(AutheticationManagerImpl.class)
                        .authenticate(new BaseConfigLoadAuthentication(new HttpCredentials("admin", "admin"),
                                new Krb5Credentials("devuser", "password", "PENTAHOQA.COM"), new SshCredentials())));
    }
}
