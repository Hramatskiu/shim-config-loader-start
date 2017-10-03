package com.epam.spring.condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DownloadConfigsCondition {
    private Map<String, Boolean> downloadedConfigsMap = new HashMap<>();

    public DownloadConfigsCondition() {
        downloadedConfigsMap.put("hdfs", false);
        downloadedConfigsMap.put("core", false);
        downloadedConfigsMap.put("yarn", false);
        downloadedConfigsMap.put("hbase", false);
        downloadedConfigsMap.put("hive", false);
        downloadedConfigsMap.put("mapreduce2", false);
    }

    public List<String> getUnloadedConfigsList() {
        return downloadedConfigsMap.entrySet().stream().filter(map -> !map.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
