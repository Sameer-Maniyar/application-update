package com.sam;

import com.sam.scheduler.Scheduler;
import com.sam.updater.CheckForUpdate;
import jakarta.xml.bind.JAXBException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws JAXBException, IOException {


//        Scheduler scheduler= new Scheduler();
//        scheduler.start();

        CheckForUpdate checkForUpdate=new CheckForUpdate();

//        checkForUpdate.markUpdateIsAvailableAsTrue();


            checkForUpdate.checkIfUpdateAvailable();


//        checkForUpdate.takeBackup(DirectoryUtil.getCurrentDirectory(),DirectoryUtil.getCurrentDirectory()+ File.separator +"bkp","Test2-Updated.jar");
//        checkForUpdate.renameUpdatedApp(DirectoryUtil.getCurrentDirectory(),"old.jar","new.jar");
//        checkForUpdate.startUpdatedApp("C:\\Users\\vitthals\\Desktop\\Sameer\\JDK\\openlogic-openjdk-17.0.14+7-windows-x64\\bin\\java",DirectoryUtil.getCurrentDirectory()+File.separator+"new.jar");

    }
}