package com.epam.spring.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileCommonUtil {
    public static void writeByteArrayToFile(String dest, byte[] input) {
        try {
            Date date = new Date();
            long start = date.getTime();
            FileUtils.writeByteArrayToFile(new File(dest), input);
            date = new Date();
            System.out.println("tt- " + String.valueOf(start - date.getTime()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
