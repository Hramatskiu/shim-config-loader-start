package com.epam.spring;

import com.epam.kerberos.HadoopKerberosUtil;
import com.epam.spring.authenticate.impl.BaseConfigLoadAuthentication;
import com.epam.spring.component.HttpDownloadService;
import com.epam.spring.component.SpringComponent2;
import com.epam.spring.component.SpringComponent3;
import com.epam.spring.component.SpringComponent4;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringTaskApp {
    public static void main(String[] args) throws Exception{
        /*KerberosRestTemplate restTemplate =
                new KerberosRestTemplate(null, "devuser@PENTAHOQA.COM", client);
        restTemplate.getForObject("http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HIVE/components", String.class);*/
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

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.getContext()
                .setAuthentication(applicationContext.getBean(AutheticationManagerImpl.class)
                        .authenticate(new BaseConfigLoadAuthentication(new HttpCredentials("admin", "admin"),
                                new Krb5Credentials("devuser", "password", "PENTAHOQA.COM"), new SshCredentials())));
        thread.join();

        date = new Date();
        System.out.println(start - date.getTime());
        HttpDownloadService httpDownloadService = applicationContext.getBean(HttpDownloadService.class);

        CompletableFuture<Boolean> hdfsTask = httpDownloadService.loadConfigsFromUri(
                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HDFS/components/HDFS_CLIENT?format=client_config_zip", "hdfs.zip");

        CompletableFuture<Boolean> yarnTask = httpDownloadService.loadConfigsFromUri(
                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/YARN/components/YARN_CLIENT?format=client_config_tar", "yarn.tar");

        CompletableFuture<Boolean> hbaseTask = httpDownloadService.loadConfigsFromUri(
                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HBASE/components/HBASE_CLIENT?format=client_config_tar", "hbase.tar");

        CompletableFuture<Boolean> hiveTask = httpDownloadService.loadConfigsFromUri(
                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HIVE/components/HIVE_CLIENT?format=client_config_tar", "hive.tar");

        List<Boolean> results = Stream.of(hdfsTask, yarnTask, hbaseTask, hiveTask).map(CompletableFuture::join).collect(Collectors.toList());
        results.forEach(System.out::println);
//        CompletableFuture<Void> combinedFuture
//                = CompletableFuture.allOf(hdfsTask, yarnTask, hbaseTask, hiveTask);
//
//        combinedFuture.get();

//        httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HDFS/components/HDFS_CLIENT?format=client_config_tar", "hdfs.tar").get();
//        httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/YARN/components/YARN_CLIENT?format=client_config_tar", "yarn.tar").get();
//        httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HBASE/components/HBASE_CLIENT?format=client_config_tar", "hbase.tar").get();
//        httpDownloadService.loadConfigsFromUri(
//                "http://svqxbdcn6hdp26secn1.pentahoqa.com:8080/api/v1/clusters/HDP26Secure/services/HIVE/components/HIVE_CLIENT?format=client_config_tar", "hive.tar").get();

        date = new Date();
        System.out.println(start - date.getTime());
    }
}
