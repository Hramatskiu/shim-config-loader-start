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

@Component("cdh-plan")
public class CDHDownloadPlan extends DownloadPlan {
    @Autowired
    private DownloadFunction downloadFunction;
    @Autowired
    @Qualifier("CDH")
    private SearchStrategy searchStrategy;

    @Override
    public boolean downloadConfigs(String hostName, String destPrefix) throws Exception {
        DownloadConfigsCondition downloadConfigsCondition = createDownloadConfigsCondition();
        downloadFunction.downloadConfigs(downloadConfigsCondition, searchStrategy,
                new LoadPathConfig(hostName + ":7180/api/v10/", destPrefix, FileExtractingService.ExtractFormats.ZIP));

        return downloadConfigsCondition.getUnloadedConfigsList().isEmpty();
    }

    private DownloadConfigsCondition createDownloadConfigsCondition() {
        DownloadConfigsCondition downloadConfigsCondition = new DownloadConfigsCondition();

        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("hdfs", Arrays.asList("hdfs-site.xml", "core-site.xml")));
        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("yarn", Arrays.asList("yarn-site.xml", "mapred-site.xml")));
        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("hbase", Collections.singletonList("hbase-site.xml")));
        downloadConfigsCondition.addConfigFilesToMap(new DownloadableFile("hive", Collections.singletonList("hive-site.xml")));

        return downloadConfigsCondition;
    }
}
