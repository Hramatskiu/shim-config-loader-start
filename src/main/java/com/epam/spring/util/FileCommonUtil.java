package com.epam.spring.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileCommonUtil {
    public static void writeByteArrayToFile(String dest, byte[] input) {
        try {
            FileUtils.writeByteArrayToFile(new File(dest), input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
