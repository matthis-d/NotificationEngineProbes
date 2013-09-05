package com.notificationengine.probes.custom;


import com.notificationengine.probes.Launcher;
import org.apache.log4j.Logger;

public class Main {

    public static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        String contextName = "fileContentProbeApplicationContext.xml";

        Launcher launcher = new Launcher(contextName);

        launcher.start();

    }

}
