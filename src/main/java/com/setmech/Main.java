package com.setmech;

import com.setmech.updater.CheckForUpdate;
import jakarta.xml.bind.JAXBException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws JAXBException, IOException {


        CheckForUpdate checkForUpdate=new CheckForUpdate();
        checkForUpdate.checkIfUpdateAvailable();


    }
}