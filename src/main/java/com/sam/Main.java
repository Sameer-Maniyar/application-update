package com.sam;

import com.sam.updater.CheckForUpdate;
import com.sam.updater.DirectoryUtil;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws JAXBException, IOException {

        CheckForUpdate checkForUpdate=new CheckForUpdate();
//        checkForUpdate.checkIfUpdateAvailable();
        checkForUpdate.takeBackup(DirectoryUtil.getCurrentDirectory(),DirectoryUtil.getCurrentDirectory()+ File.separator +"bkp","Test2-Updated.jar");

    }
}