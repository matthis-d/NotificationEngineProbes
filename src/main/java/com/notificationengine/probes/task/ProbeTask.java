package com.notificationengine.probes.task;


import org.apache.log4j.Logger;
import com.notificationengine.probes.probe.IProbe;

import java.util.TimerTask;

public class ProbeTask extends TimerTask{

    public static Logger LOGGER = Logger.getLogger(ProbeTask.class);

    private IProbe probe;

    public ProbeTask(IProbe probe) {

        LOGGER.info("Instantiating ProbeTask with probe " + probe.getClass().getName());

        this.probe = probe;

        LOGGER.info("ProbeTask instantiated : "  + probe.getClass().getName());
    }

    @Override
    public void run() {

        LOGGER.info("Launching ProbeTask : " + probe.getClass().getName());

        this.probe.listen();

        LOGGER.info("ProbeTask has finished : " + probe.getClass().getName());

    }
}
