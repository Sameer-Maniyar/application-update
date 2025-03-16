package com.sam;

import com.sam.updater.CheckForUpdate;
import com.sam.updater.DirectoryUtil;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws JAXBException, IOException {

        CheckForUpdate checkForUpdate=new CheckForUpdate();
        checkForUpdate.checkIfUpdateAvailable();
        checkForUpdate.takeBackup(DirectoryUtil.getCurrentDirectory(),DirectoryUtil.getCurrentDirectory()+ File.separator +"bkp","Test2-Updated.jar");
        checkForUpdate.renameUpdatedApp(DirectoryUtil.getCurrentDirectory(),"old.jar","new.jar");
        checkForUpdate.startUpdatedApp("C:\\Users\\vitthals\\Desktop\\Sameer\\JDK\\openlogic-openjdk-17.0.14+7-windows-x64\\bin\\java",DirectoryUtil.getCurrentDirectory()+File.separator+"new.jar");

    }
}