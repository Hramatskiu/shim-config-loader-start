package com.epam.spring.util;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Date;
import java.util.List;

public class FileCommonUtil {
    public static void writeByteArrayToFile(String dest, byte[] input) {
        try {
            FileUtils.writeByteArrayToFile(new File(dest), input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeStringToFile(String dest, String input) {
        try {
            FileUtils.writeStringToFile(new File(dest), input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extractFilesFromTarArchiveByteArray(byte[] source, List<String> unpackingFileNames) throws Exception{
        try(TarArchiveInputStream debInputStream =
                    new TarArchiveInputStream( new GzipCompressorInputStream(
                            new ByteArrayInputStream(source)));
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TarArchiveEntry entry = null;
            while ((entry = debInputStream.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    if (unpackingFileNames.contains(entry.getName())) {
                        out.reset();
                        byte[] buffer = new byte[15000];
                        int len;
                        while ((len = debInputStream.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                        writeStringToFile(entry.getName(), out.toString());
                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
