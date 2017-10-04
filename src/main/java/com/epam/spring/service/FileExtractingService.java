package com.epam.spring.service;

import com.epam.spring.util.FileCommonUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class FileExtractingService {
    private Map<ExtractFormats, BiConsumer<byte[], List<String>>> extractingFunctionsMap;

    public FileExtractingService() {
        extractingFunctionsMap = new HashMap<>();
        extractingFunctionsMap.put(ExtractFormats.ZIP, (bytes, strings) -> {
            try {
                FileCommonUtil.extractFilesFromZipArchiveByteArray(bytes, strings);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        extractingFunctionsMap.put(ExtractFormats.TAR, (bytes, strings) -> {
            try {
                FileCommonUtil.extractFilesFromTarArchiveByteArray(bytes, strings);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public BiConsumer<byte[], List<String>> getExtractFunction(ExtractFormats extractFormat) {
        return extractingFunctionsMap.getOrDefault(extractFormat, (bytes, strings) -> {
            //empty
        });
    }

    public enum ExtractFormats {
        ZIP, TAR
    }
}
