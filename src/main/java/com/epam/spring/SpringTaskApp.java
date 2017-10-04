package com.epam.spring;

import com.epam.kerberos.HadoopKerberosUtil;
import com.epam.spring.authenticate.impl.BaseConfigLoadAuthentication;
import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.function.impl.HttpDownloadFunction;
import com.epam.spring.service.download.HttpDownloadService;
import com.epam.spring.config.HttpCredentials;
import com.epam.spring.config.Krb5Credentials;
import com.epam.spring.config.SpringAppConfig;
import com.epam.spring.config.SshCredentials;
import com.epam.spring.security.AutheticationManagerImpl;
import com.epam.spring.util.CommonUtilHolder;
import com.epam.spring.util.FileCommonUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringTaskApp {
    public static void main(String[] args) throws Exception{
        Date date = new Date();
        long start = date.getTime();
        System.out.println(date.getTime());

        Krb5Credentials krb5Credentials = new Krb5Credentials("devuser", "password", "PENTAHOQA.COM");
        Thread thread = new Thread(() -> {
            try {
                //Thread.sleep(10000);
                HadoopKerberosUtil.doLogin(krb5Credentials.getUsername(), krb5Credentials.getPassword()).getSubject();
            } catch (LoginException | IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringAppConfig.class);
        date = new Date();
        System.out.println("tt- " + String.valueOf(start - date.getTime()));
        thread.join();

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.getContext()
                .setAuthentication(applicationContext.getBean(AutheticationManagerImpl.class)
                        .authenticate(new BaseConfigLoadAuthentication(new HttpCredentials("admin", "admin"),
                                new Krb5Credentials("devuser", "password", "PENTAHOQA.COM"), new SshCredentials())));


        date = new Date();
        System.out.println(start - date.getTime());
//        FileCommonUtil.writeByteArrayToFile("test.json", IOUtils.toByteArray(CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
//                .execute(CommonUtilHolder.httpCommonUtilInstance()
//                        .createHttpUriRequest("http://svqxbdcn6hdp26secn1.pentahoqa.com:6080/service/public/api/policy?repositoryType=hdfs")).getEntity().getContent()));
//        HttpDownloadFunction httpDownloadFunction = applicationContext.getBean(HttpDownloadFunction.class);
//        httpDownloadFunction.downloadConfigs("svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/", new DownloadConfigsCondition(), "TAR");

        HttpDownloadFunction httpDownloadFunction = applicationContext.getBean(HttpDownloadFunction.class);
        httpDownloadFunction.downloadConfigs("svqxbdcn6cdh512secn1.pentahoqa.com:7180/api/v10/", new DownloadConfigsCondition(), "ZIP");
//        HttpDownloadService httpDownloadService = applicationContext.getBean(HttpDownloadService.class);
//
//        CompletableFuture<Boolean> hdfsTask = httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HDFS/components/HDFS_CLIENT?format=client_config_tar",
//                Arrays.asList("hdfs-site.xml", "core-site.xml"));
//
//        CompletableFuture<Boolean> yarnTask = httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/YARN/components/YARN_CLIENT?format=client_config_tar",
//                Collections.singletonList("yarn-site.xml"));
//
//        CompletableFuture<Boolean> hbaseTask = httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HBASE/components/HBASE_CLIENT?format=client_config_tar",
//                Collections.singletonList("hbase-site.xml"));
//
//        CompletableFuture<Boolean> hiveTask = httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HIVE/components/HIVE_CLIENT?format=client_config_tar",
//                Collections.singletonList("hive-site.xml"));
//
//        List<Boolean> results = Stream.of(hdfsTask, yarnTask, hbaseTask, hiveTask).map(CompletableFuture::join).collect(Collectors.toList());
//        results.forEach(System.out::println);

        date = new Date();
        System.out.println(start - date.getTime());
    }
}
