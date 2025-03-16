package com.setmech.updater;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.*;

@Slf4j
public class CheckForUpdate {

    String URL = "http://localhost:9090/updates/product";
    private final String UPDATE_METADATA_FOLDER_PATH = "MetaData";
    private final String XML_FILE_NAME = "update_meta_data.xml";

    void startDownload(Stage stage1) {
        FileDownloadTask fileDownloadTask = new FileDownloadTask("https://github.com/Sameer-Maniyar/application-update/archive/refs/heads/main.zip", "C:\\Users\\sameermaniyar\\Desktop\\bkp", stage1);
        new Thread(fileDownloadTask).start();
//        progressBar.progressProperty().bind(fileDownloadTask.progressProperty());
    }


    static String LOCAL_JAR = "";
    static String TEMP_JAR = "";

    private static void replaceJar() throws IOException, IOException {
        Path oldJar = Paths.get(LOCAL_JAR);
        Path newJar = Paths.get(TEMP_JAR);

        // Backup old JAR before replacing (optional)
        Files.move(oldJar, Paths.get(LOCAL_JAR + ".backup"), StandardCopyOption.REPLACE_EXISTING);

        // Move new JAR to replace the old one
        Files.move(newJar, oldJar, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Update applied successfully!");
    }


    public void downloadXmlFileFromServer() {

        try {


            log.info("checking if new updates are available URL: {}", URL);

            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            // Check if the connection is successful by getting the response code
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("Failed to connect to the server. Response code: {}", responseCode);
                return; // Exit or continue based on your needs
            }


            byte[] data;
            try (InputStream inputStream = connection.getInputStream()) {
                data = inputStream.readAllBytes(); // Read response
            }

            Path filePath = Path.of(getMetaDataFolderPath(), XML_FILE_NAME);

            log.info("update xml directory path path:{}", filePath.toString());


            // Save XML as a file
            Files.write(filePath, data, StandardOpenOption.CREATE);

            connection.disconnect();

        } catch (UnknownHostException e) {
            // This exception is thrown if the host is unreachable
            log.error("Network error: Unable to reach the server at URL: {}. Please check your connection.", URL, e);

        } catch (IOException e) {
            // This exception is thrown for general I/O issues (e.g., server not responding)
            log.error("I/O error occurred while connecting to URL: {}", URL, e);

        } catch (Exception e) {
            // Catch other unexpected exceptions
            log.error("An unexpected error occurred.", e);
        }
    }


    private String getCurrentDirectory() throws IOException {


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

    private String getMetaDataFolderPath() throws IOException {


        Path parentDir = Path.of(getCurrentDirectory(), UPDATE_METADATA_FOLDER_PATH);

        if (Files.notExists(parentDir)) {
            Files.createDirectories(parentDir);  // Create directories if they do not exist
            log.info("Created missing directories at: {}", parentDir);
        }

        return parentDir.toString();

    }


    UpdateMetaData loadLocalUpdateMetaData() throws IOException, JAXBException {


        UpdateMetaData updateMetaData = null;
        Path parentDir = Path.of(getCurrentDirectory(), UPDATE_METADATA_FOLDER_PATH, XML_FILE_NAME);


        if (!Files.exists(parentDir)) {
            log.error("The file does not exist at: {}", parentDir);
            downloadXmlFileFromServer();
        }

        log.info("loading local xml file from directory: {}", parentDir);


        try (InputStream inputStream = Files.newInputStream(parentDir)) {  // Correct way to get InputStream from Path

            // JAXB unmarshalling
            JAXBContext jaxbContext = JAXBContext.newInstance(UpdateMetaData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            updateMetaData = (UpdateMetaData) unmarshaller.unmarshal(inputStream);  // Directly unmarshalling from InputStream

            // Process the 'product' object as needed
            log.debug("Product Data: " + updateMetaData);

        } catch (JAXBException e) {

            log.error("Error during unmarshalling", e);
            throw e;

        } catch (IOException e) {

            log.error("Error reading the file", e);
            throw e;
        }

        return updateMetaData;
    }


    public Boolean checkIfUpdateAvailable() throws JAXBException, IOException {
        UpdateMetaData serverUpdateMetaData = loadCurrentUpdateMetaDataFromServer();
        UpdateMetaData localUpdateMetaData = loadLocalUpdateMetaData();

        if (!localUpdateMetaData.compare(serverUpdateMetaData)) {

            log.info("New Update available............");

        } else {
            log.info("Application is already up to date.......... ");
        }

        log.info("Local hash: {}   Server hash:{}",localUpdateMetaData.getCheckSum(),serverUpdateMetaData.getCheckSum());

        return true;
    }

    UpdateMetaData loadCurrentUpdateMetaDataFromServer() throws IOException, JAXBException {

        UpdateMetaData updateMetaData = null;
        HttpURLConnection connection = null;

        try {

            log.info("loading  xml file from server URL: {}", URL);

            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            // Check if the connection is successful by getting the response code
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("Failed to connect to the server. Response code: {}", responseCode);
                return null; // Exit or continue based on your needs
            }


            byte[] data;
            try (InputStream inputStream = connection.getInputStream()) {
                data = inputStream.readAllBytes(); // Read response
            }


            JAXBContext jaxbContext = JAXBContext.newInstance(UpdateMetaData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            updateMetaData = (UpdateMetaData) unmarshaller.unmarshal(new ByteArrayInputStream(data));


        } catch (UnknownHostException e) {
            // This exception is thrown if the host is unreachable
            log.error("Network error: Unable to reach the server at URL: {}. Please check your connection.", URL, e);

        } catch (IOException e) {
            // This exception is thrown for general I/O issues (e.g., server not responding)
            log.error("I/O error occurred while connecting to URL: {}", URL, e);

        } catch (JAXBException e) {
            // This handles any errors during XML unmarshalling
            log.error("Failed to unmarshall the XML data.", e);

        } catch (Exception e) {
            // Catch other unexpected exceptions
            log.error("An unexpected error occurred.", e);
        } finally {
            connection.disconnect();
        }


        return updateMetaData;
    }


}
