package com.josecarloscruz89.msusers.utils;

import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

public class FileUtils {

    public static String getJSONFromFile(String filename) throws Exception {
        ClassPathResource resource = new ClassPathResource(filename);

        return new String(Files.readAllBytes(resource.getFile().toPath()));
    }
}