package com.notificationengine.probes;

import org.apache.log4j.Logger;

public class Main {

    public static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        LOGGER.info("Application started");

        String contextName = "applicationContext.xml";

        Launcher launcher = new Launcher(contextName);

        launcher.start();

    }

}
