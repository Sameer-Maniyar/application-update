package com.sam.scheduler;

import com.sam.updater.UpdateMetaData;
import com.sam.updater.CheckForUpdate;
import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private ScheduledExecutorService scheduler;

    public void start() {

        try {

            scheduler = Executors.newScheduledThreadPool(1);

            scheduler.scheduleWithFixedDelay(() -> {

                CheckForUpdate checkForUpdate= new CheckForUpdate();
                try {
                    UpdateMetaData updateMetaData = checkForUpdate.loadLocalUpdateMetaData();

                    if(updateMetaData.getUpdatesApplied() ){

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Checking for task");


            }, 0, 30, TimeUnit.SECONDS);
        } catch (Exception e) {

            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();

                try {

                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }


                } catch (InterruptedException ie) {

                    scheduler.shutdownNow();
                }

            }
        }

    }


    public void stop() {


        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();

            try {

                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }


            } catch (InterruptedException ie) {

                scheduler.shutdownNow();
            }

        }


    }
}
