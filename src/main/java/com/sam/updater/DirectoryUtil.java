package com.sam.updater;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DirectoryUtil {


    public static String getCurrentDirectory() throws IOException {


        File classFile = null;
        try {
            classFile = new File(CheckForUpdate.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
        } catch (URISyntaxException e) {

            throw new RuntimeException(e);
        }

        String directoryPath = classFile.getParentFile().getAbsolutePath();


        return directoryPath;

    }
}
