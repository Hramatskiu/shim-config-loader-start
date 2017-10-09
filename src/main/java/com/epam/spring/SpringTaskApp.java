package com.epam.spring;

import com.epam.spring.config.*;
import com.epam.spring.manager.LoadConfigsManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringTaskApp {
    private ApplicationContext applicationContext;
    private static Logger logger = Logger.getLogger(SpringTaskApp.class);

    public void init() {
        applicationContext = new AnnotationConfigApplicationContext(SpringAppConfig.class);
    }

    public void loadConfigs(LoadConfigs loadConfigs) {
        LoadConfigsManager loadConfigsManager = applicationContext.getBean(LoadConfigsManager.class);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Stream.of(CompletableFuture.supplyAsync(() -> {
            try {
                return loadConfigsManager.downloadClientConfigs(loadConfigs.getClusterType(), loadConfigs);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor)).map(CompletableFuture::join).collect(Collectors.toList()).forEach(logger::info);

        executor.shutdown();
    }

    /*public static void main(String[] args) throws Exception{
        Date date = new Date();
        long start = date.getTime();
        System.out.println(date.getTime());

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringAppConfig.class);

        date = new Date();
        System.out.println(start - date.getTime());

        LoadConfigsManager loadConfigsManager = applicationContext.getBean(LoadConfigsManager.class);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Stream.of(CompletableFuture.supplyAsync(() -> {
            try {
                return loadConfigsManager.downloadClientConfigs(LoadConfigsManager.ClusterType.HDP, new LoadConfigs(new HttpCredentials("admin", "admin"),
                        new Krb5Credentials("devuser", "password"), new SshCredentials(),
                        "svqxbdcn6hdp26secn1.pentahoqa.com", "D:\\test_conf_hdp"));
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor), CompletableFuture.supplyAsync(() -> {
            try {
                return loadConfigsManager.downloadClientConfigs(LoadConfigsManager.ClusterType.CDH, new LoadConfigs(new HttpCredentials("admin", "admin"),
                        new Krb5Credentials("devuser", "password"), new SshCredentials(),
                        "svqxbdcn6cdh512secn1.pentahoqa.com", "D:\\test_conf_cdh"));
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor)/*, CompletableFuture.supplyAsync(() -> {
            try {
                return loadConfigsManager.downloadClientConfigs(LoadConfigsManager.ClusterType.CDH, new LoadConfigs(new HttpCredentials("admin", "admin"),
                        new Krb5Credentials("devuser", "password"), new SshCredentials(),
                        "svqxbdcn6cdh512secn1.pentahoqa.com", "D:\\test_conf_cdh_2"));
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor), CompletableFuture.supplyAsync(() -> {
            try {
                return loadConfigsManager.downloadClientConfigs(LoadConfigsManager.ClusterType.CDH, new LoadConfigs(new HttpCredentials("admin", "admin"),
                        new Krb5Credentials("devuser", "password"), new SshCredentials(),
                        "svqxbdcn6cdh512secn1.pentahoqa.com", "D:\\test_conf_cdh_3"));
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor)).map(CompletableFuture::join).collect(Collectors.toList()).forEach(System.out::println);

        executor.shutdown();
        date = new Date();
        System.out.println(start - date.getTime());
    }*/
}
