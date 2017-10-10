package com.epam.spring.search.impl;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.constant.DownloadableFileConstants;
import com.epam.spring.search.SearchStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MaprDefaultSearchStrategyCommand implements SearchStrategy {
    @Override
    public String getStrategyCommand() {
        return "hadoop version";
    }

    @Override
    public List<DownloadableFile> resolveCommandResult(String commandResult, List<DownloadableFile> searchableServiceNames) throws Exception {
        String hadoopVersion = extractHadoopVersionFromCommandResult(commandResult);
        searchableServiceNames.forEach(service -> {
            if (DownloadableFileConstants.ServiceName.HIVE.equals(service.getServiceName()) || DownloadableFileConstants.ServiceName.HBASE.equals(service.getServiceName())){
                service.setDownloadPath("$MAPR_HOME/" + service.getServiceName() + "/$(ls $MAPR_HOME/" + service.getServiceName() + ")/conf");
            }
            else {
                service.setDownloadPath("$MAPR_HOME/hadoop/hadoop-" + hadoopVersion + "/etc/hadoop/");
            }
        });

        return searchableServiceNames;
    }

    private String extractHadoopVersionFromCommandResult(String commandResult) throws Exception{
        return Arrays.stream(Stream.of(commandResult.split(" "))
                .filter(splitCommand -> splitCommand.contains("mapr")).findFirst().orElse(StringUtils.EMPTY).split("-"))
                .findFirst().orElse(StringUtils.EMPTY);
    }
}
