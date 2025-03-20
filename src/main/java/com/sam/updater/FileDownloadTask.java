package com.sam.updater;

import javafx.concurrent.Task;
import javafx.stage.Popup;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class FileDownloadTask extends Task<Void> {
    private final String fileURL;
    private final String saveLocation;
    private final Popup popup;

    public FileDownloadTask(String fileURL, String saveLocation, Popup popup) {
        this.fileURL = fileURL;
        this.saveLocation = saveLocation;
        this.popup = popup;
    }

    @Override
    protected Void call() throws Exception {
        URL url = new URL(fileURL);
        log.info("URL to download updates URL:{}", url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String fileName = getFileName(connection, fileURL);

        File saveFile = new File(saveLocation, fileName);
        log.info("location to save downloaded file:{}", saveFile);

        int responseCode = connection.getResponseCode();
        log.info("response code :{}", responseCode);

        if (responseCode != HttpURLConnection.HTTP_OK) {
            log.error("Download failed: Server returned HTTP code {}", responseCode);
            updateMessage("Download failed: HTTP code " + responseCode);
            if (popup != null) {
                popup.hide();  // Close the popup when download failed
            }
            cancel();  // Cancel task
            return null;
        }

        long totalSize = connection.getContentLengthLong();  // Get file size
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(saveFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            double downloadedSize = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (isCancelled()) {
                    log.info("Download cancelled by user");
                    return null;  // Exit if the task is cancelled
                }

                outputStream.write(buffer, 0, bytesRead);
                downloadedSize += bytesRead;
                updateProgress(downloadedSize / totalSize, 1);
            }
        } catch (Exception e) {
            log.error("Error during download", e);
            updateMessage("Download failed: " + e.getMessage());
            cancel();  // Cancel task on error
        }

        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        if (popup != null) {
            popup.hide();  // Close the popup when download succeeds
        }
        updateMessage("Download completed successfully!");
    }

    @Override
    protected void failed() {
        super.failed();
        if (popup != null) {
            popup.hide();  // Hide popup when the download fails
        }
        updateMessage("Download failed: " + getException().getMessage());
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
