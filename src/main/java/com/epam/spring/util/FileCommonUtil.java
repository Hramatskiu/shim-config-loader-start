package com.epam.spring.util;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
            //Files.write(Paths.get(dest), Collections.singletonList(input));
            System.out.println("Save - " + dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extractFilesFromTarArchiveByteArray(byte[] source, List<String> unpackingFileNames, String destPrefix) throws Exception{
        try(TarArchiveInputStream debInputStream =
                    new TarArchiveInputStream( new GzipCompressorInputStream(
                            new ByteArrayInputStream(source)))) {
            TarArchiveEntry entry = null;
            while ((entry = debInputStream.getNextTarEntry()) != null) {
                saveEntry(entry, debInputStream, unpackingFileNames, destPrefix);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void extractFilesFromZipArchiveByteArray(byte[] source, List<String> unpackingFileNames, String destPrefix) throws Exception{
        try(ZipArchiveInputStream debInputStream =
                    new ZipArchiveInputStream(
                            new ByteArrayInputStream(source))) {
            ZipArchiveEntry entry = null;
            while ((entry = debInputStream.getNextZipEntry()) != null) {
                saveEntry(entry, debInputStream, unpackingFileNames, destPrefix);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void saveEntry(ArchiveEntry entry, ArchiveInputStream archiveInputStream, List<String> unpackingFileNames, String destPrefix) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (!entry.isDirectory()) {
                List<String> matchingFileNames = unpackingFileNames.stream().filter(fileName -> entry.getName().contains(fileName)).collect(Collectors.toList());
                if (!matchingFileNames.isEmpty()) {
                    byte[] buffer = new byte[15000];
                    int len;
                    while ((len = archiveInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    writeStringToFile(destPrefix + "\\" + matchingFileNames.get(0), out.toString());
                }
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
