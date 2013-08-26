package org.notificationengine.probes;

import org.apache.log4j.Logger;

import java.nio.file.*;
import java.util.List;


public class Launcher {

    static Logger LOGGER = Logger.getLogger(Launcher.class);

    public static void main(String[] args) {

        LOGGER.info("Start the launcher");

        //define a folder root
        Path myDir = Paths.get("/Users/Matthis/tmp");

        while(Boolean.TRUE) {
            try {
                WatchService watcher = myDir.getFileSystem().newWatchService();
                myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

                WatchKey watckKey = watcher.take();

                List<WatchEvent<?>> events = watckKey.pollEvents();


                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        LOGGER.info("Created: " + event.context().toString());
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        LOGGER.info("Delete: " + event.context().toString());
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        LOGGER.info("Modify: " + event.context().toString());
                    }
                }

            } catch (Exception e) {
                LOGGER.warn("Error: " + e.toString());
            }
        }


    }

}
