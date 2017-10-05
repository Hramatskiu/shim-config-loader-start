package com.epam.spring.plan;

import com.epam.spring.service.FileExtractingService;

import java.util.ArrayList;
import java.util.List;

public abstract class DownloadPlan {
    public abstract boolean downloadConfigs(String hostName, String destPrefix) throws Exception;

    public static class LoadPathConfig {
        private String compositeHost;
        private String destPrefix;
        private List<String> loadedFiles;
        private FileExtractingService.ExtractFormats extractFormat;

        public LoadPathConfig(String compositeHost, String destPrefix, FileExtractingService.ExtractFormats extractFormat) {
            this.compositeHost = compositeHost;
            this.destPrefix = destPrefix;
            this.extractFormat = extractFormat;
            this.loadedFiles = new ArrayList<>();
        }

        public String getCompositeHost() {
            return compositeHost;
        }

        public void setCompositeHost(String compositeHost) {
            this.compositeHost = compositeHost;
        }

        public String getDestPrefix() {
            return destPrefix;
        }

        public void setDestPrefix(String destPrefix) {
            this.destPrefix = destPrefix;
        }

        public List<String> getLoadedFiles() {
            return loadedFiles;
        }

        public void setLoadedFiles(List<String> loadedFiles) {
            this.loadedFiles = loadedFiles;
        }

        public FileExtractingService.ExtractFormats getExtractFormat() {
            return extractFormat;
        }

        public void setExtractFormat(FileExtractingService.ExtractFormats extractFormat) {
            this.extractFormat = extractFormat;
        }
    }
}
