package com.notificationengine.probes;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Matthis
 * Date: 05/09/13
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        LOGGER.info("Application started");

        String contextName = "applicationContext.xml";

        Launcher launcher = new Launcher(contextName);

        launcher.start();

    }

}
