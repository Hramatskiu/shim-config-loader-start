package com.epam.spring.condition;

import java.util.*;
import java.util.stream.Collectors;

public class DownloadConfigsCondition {
    private Map<DownloadableFile, Boolean> downloadedConfigsMap = new HashMap<>();

    public DownloadConfigsCondition() {
//        downloadedConfigsMap.put(new DownloadableFile("hdfs", Arrays.asList("hdfs-site.xml", "core-site.xml")), false);
//        downloadedConfigsMap.put(new DownloadableFile("yarn", Arrays.asList("yarn-site.xml", "mapred-site.xml")), false);
//        downloadedConfigsMap.put(new DownloadableFile("hbase", Collections.singletonList("hbase-site.xml")), false);
//        downloadedConfigsMap.put(new DownloadableFile("hive", Collections.singletonList("hive-site.xml")), false);
//        //downloadedConfigsMap.put(new DownloadableFile("mapreduce2", Collections.singletonList("mapred-site.xml")), false);
    }

    public void addConfigFilesToMap(DownloadableFile downloadableFile) {
        if (!downloadableFile.getServiceName().isEmpty() &&
                downloadedConfigsMap.entrySet().stream().filter(entry -> entry.getKey().getServiceName().equals(downloadableFile.getServiceName())).collect(Collectors.toList()).isEmpty()) {
            downloadedConfigsMap.put(downloadableFile, false);
        }
    }

    public List<DownloadableFile> getUnloadedConfigsList() {
        return downloadedConfigsMap.entrySet().stream().filter(map -> !map.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public void setDownloadCondition(List<Boolean> updatedConditions) {
        Iterator<Boolean> conditionsIterator = updatedConditions.iterator();
        downloadedConfigsMap.replaceAll((k, v) -> conditionsIterator.hasNext() & !v ? conditionsIterator.next() : v);
    }
}
