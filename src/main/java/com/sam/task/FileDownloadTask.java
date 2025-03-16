package com.sam.task;


import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloadTask extends Task<Void> {
    private final String fileURL;
    private final String saveLocation;
    private final Stage stage ;

    public FileDownloadTask(String fileURL, String saveLocation, Stage stage) {
        this.fileURL = fileURL;
        this.saveLocation = saveLocation;
        this.stage = stage;
    }

    @Override
    protected Void call() throws Exception {
        URL url = new URL(fileURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");


        String fileName = getFileName(connection, fileURL);

        File saveFile = new File(saveLocation, fileName);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            long totalSize = connection.getContentLengthLong();  // Get file size
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(saveFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                double downloadedSize = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    downloadedSize += bytesRead;
                    updateProgress(downloadedSize/totalSize, 1);
                }
            }
        }
        return null;
    }


    @Override
    protected void succeeded() {
        super.succeeded();
        if (stage != null) {
            stage.close();  // ✅ Close the window when download succeeds
        }
    }


    private static String getFileName(HttpURLConnection connection, String fileURL) {
        String fileName = null;

        // Check Content-Disposition header for filename
        String contentDisposition = connection.getHeaderField("Content-Disposition");
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            fileName = contentDisposition.split("filename=")[1].replaceAll("\"", "");
        }

        // If no filename in header, extract from URL
        if (fileName == null) {
            fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        }

        return fileName;
    }
}
