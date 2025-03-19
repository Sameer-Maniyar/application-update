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



    }
}