package com.epam.spring.plan.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.service.FileExtractingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Component("hdp-plan")
public class HDPDownloadPlan extends DownloadPlan {
    @Autowired
    private DownloadFunction downloadFunction;
    @Autowired
    @Qualifier("HDP")
    private SearchStrategy searchStrategy;

    public boolean downloadConfigs(String hostName, String destPrefix) throws Exception{
        DownloadConfigsCondition downloadConfigsCondition = createDownloadConfigsCondition();
        downloadFunction.downloadConfigs(downloadConfigsCondition, searchStrategy,
                new LoadPathConfig(hostName + ":8080/api/v1/", destPrefix, FileExtractingService.ExtractFormats.TAR));

        return downloadConfigsCondition.getUnloadedConfigsList().isEmpty();
    }

    private DownloadConfigsCondition createDownloadConfigsCondition() {
        DownloadConfigsCondition downloadConfigsCondition = new DownloadConfigsCondition();

        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("hdfs", Arrays.asList("hdfs-site.xml", "core-site.xml")));
        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("yarn", Collections.singletonList("yarn-site.xml")));
        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("hbase", Collections.singletonList("hbase-site.xml")));
        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("hive", Collections.singletonList("hive-site.xml")));
        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("mapreduce2", Collections.singletonList("mapred-site.xml")));

        return downloadConfigsCondition;
    }
}
