package com.sam.updater;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.*;

@Slf4j
public class CheckForUpdate {

    String URL = "http://localhost:9090/updates/product";
    private final String UPDATE_METADATA_FOLDER_PATH = "MetaData";
    private final String XML_FILE_NAME = "update_meta_data.xml";

//    void startDownload(Stage stage1) {
//        FileDownloadTask fileDownloadTask = new FileDownloadTask("https://github.com/Sameer-Maniyar/application-update/archive/refs/heads/main.zip", "C:\\Users\\sameermaniyar\\Desktop\\bkp", stage1);
//        new Thread(fileDownloadTask).start();
////        progressBar.progressProperty().bind(fileDownloadTask.progressProperty());
//    }





    public void startUpdateApplier(String javaPath, String jarFilePath) throws IOException {

        // Create a process to run the JAR file
        ProcessBuilder processBuilder = new ProcessBuilder(javaPath, "-jar", jarFilePath);

        try {
            // Start the process (run the JAR file)
            Process process = processBuilder.start();
            log.info("JAR file is starting...");

            // Optionally, you can wait for the process to finish
//            int exitCode = process.waitFor();
//            log.info("Process finished with exit code: " + exitCode);
        } catch (IOException e) {
            // Handle exceptions
            log.error("An error occurred while starting the JAR file.");
            e.printStackTrace();
        }


    }



    public Boolean downloadXmlFileFromServer() {

        try {

            log.info("downloading Xml File From Server URL : {}", URL);

            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            // Check if the connection is successful by getting the response code
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("Failed to connect to the server. Response code: {}", responseCode);
                return false; // Exit or continue based on your needs
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

        } catch (ConnectException e) {

            log.error("connection refused check if server is down or something");
            return false;
        } catch (UnknownHostException e) {
            // This exception is thrown if the host is unreachable
            log.error("Network error: Unable to reach the server at URL: {}. Please check your connection.", URL, e);
            return false;
        } catch (IOException e) {
            // This exception is thrown for general I/O issues (e.g., server not responding)
            log.error("I/O error occurred while connecting to URL: {}", URL, e);
            return false;

        } catch (Exception e) {
            // Catch other unexpected exceptions
            log.error("An unexpected error occurred.", e);
            return false;

        }

        return true;
    }


    private String getMetaDataFolderPath() throws IOException {


        Path parentDir = Path.of(DirectoryUtil.getCurrentDirectory(), UPDATE_METADATA_FOLDER_PATH);

        if (Files.notExists(parentDir)) {
            Files.createDirectories(parentDir);  // Create directories if they do not exist
            log.info("Created missing directories at: {}", parentDir);
        }

        return parentDir.toString();

    }


    public UpdateMetaData loadLocalUpdateMetaData() throws IOException, JAXBException {

        log.error("loading of local update meta data started");
        UpdateMetaData updateMetaData = null;
        Path parentDir = Path.of(DirectoryUtil.getCurrentDirectory(), UPDATE_METADATA_FOLDER_PATH, XML_FILE_NAME);


        if (!Files.exists(parentDir)) {
            log.error("The file does not exist at: {}", parentDir);

            if (!downloadXmlFileFromServer()) {
                return null;
            }
            ;
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


        } catch (IOException e) {

            log.error("Error reading the file", e);

        }

        return updateMetaData;
    }


    public Boolean checkIfUpdateAvailable() throws JAXBException, IOException {
        UpdateMetaData serverUpdateMetaData = loadCurrentUpdateMetaDataFromServer();
        UpdateMetaData localUpdateMetaData = loadLocalUpdateMetaData();
        boolean isUpdateAvaialable = !localUpdateMetaData.compare(serverUpdateMetaData);
        if (localUpdateMetaData == null || serverUpdateMetaData==null) {

            log.info("cannot compare if update available cause loading local update meta data is null or server update meta data is null --- checkIfUpdateAvailable() ");

            return false;

        } else if (isUpdateAvaialable) {

            log.info("New Update available............");

        } else {
            log.info("Application is already up to date.......... ");
        }


        log.info("Local hash: {}   Server hash:{}", localUpdateMetaData != null ? localUpdateMetaData.getCheckSum() : null, serverUpdateMetaData != null ? serverUpdateMetaData.getCheckSum() : null);


        return isUpdateAvaialable;
    }

    UpdateMetaData loadCurrentUpdateMetaDataFromServer() {

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


        } catch (ConnectException e) {

            log.error("connection refused check if server is down or something");

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


            if (connection != null) {
                connection.disconnect();
            }
        }


        return updateMetaData;
    }


    public UpdateMetaData markUpdateApplied() throws JAXBException, IOException {
        UpdateMetaData updateMetaData = null;
        Path parentDir = Path.of(DirectoryUtil.getCurrentDirectory(), UPDATE_METADATA_FOLDER_PATH, XML_FILE_NAME);

        log.info("Loading local XML file from directory: {}", parentDir);

        try (InputStream inputStream = Files.newInputStream(parentDir)) {
            // JAXB unmarshalling
            JAXBContext jaxbContext = JAXBContext.newInstance(UpdateMetaData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            updateMetaData = (UpdateMetaData) unmarshaller.unmarshal(inputStream);  // Unmarshalling XML to Java object

            // Example of modifying the Java object (e.g., setting a new attribute)
            if (updateMetaData != null) {

                updateMetaData.setUpdatesApplied(true);


                log.debug("Updated Product Data: " + updateMetaData);
            }
        } catch (JAXBException e) {
            log.error("Error during unmarshalling", e);
            throw e;
        } catch (IOException e) {
            log.error("Error reading the file", e);
            throw e;
        }

        // After making the changes, save the updated object back to XML
        saveUpdatedXml(updateMetaData, parentDir);

        return updateMetaData;
    }


    public UpdateMetaData markUpdateDownloaded() throws JAXBException, IOException {
        UpdateMetaData updateMetaData = null;
        Path parentDir = Path.of(DirectoryUtil.getCurrentDirectory(), UPDATE_METADATA_FOLDER_PATH, XML_FILE_NAME);

        log.info("Loading local XML file from directory: {}", parentDir);

        try (InputStream inputStream = Files.newInputStream(parentDir)) {
            // JAXB unmarshalling
            JAXBContext jaxbContext = JAXBContext.newInstance(UpdateMetaData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            updateMetaData = (UpdateMetaData) unmarshaller.unmarshal(inputStream);  // Unmarshalling XML to Java object

            // Example of modifying the Java object (e.g., setting a new attribute)
            if (updateMetaData != null) {

                UpdateMetaData updateMetaData1 = loadCurrentUpdateMetaDataFromServer();

                updateMetaData.setUpdatedJarDownloaded(true);
                updateMetaData.setUpdatesApplied(false);
                updateMetaData.setCheckSum(updateMetaData1.getCheckSum());
                updateMetaData.setFileName(updateMetaData1.getFileName());
                updateMetaData.setVersionNumber(updateMetaData1.getVersionNumber());

                log.debug("Updated Product Data: " + updateMetaData);
            }
        } catch (JAXBException e) {
            log.error("Error during unmarshalling", e);
            throw e;
        } catch (IOException e) {
            log.error("Error reading the file", e);
            throw e;
        }

        // After making the changes, save the updated object back to XML
        saveUpdatedXml(updateMetaData, parentDir);

        return updateMetaData;
    }

    private void saveUpdatedXml(UpdateMetaData updateMetaData, Path filePath) {
        try {
            // Marshal the updated Java object back to XML
            JAXBContext jaxbContext = JAXBContext.newInstance(UpdateMetaData.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Save the updated XML to the file
            try (OutputStream outputStream = Files.newOutputStream(filePath)) {
                marshaller.marshal(updateMetaData, outputStream);
                log.info("XML file updated successfully: {}", filePath);
            }
        } catch (JAXBException | IOException e) {
            log.error("Error saving updated XML", e);
        }
    }


}
