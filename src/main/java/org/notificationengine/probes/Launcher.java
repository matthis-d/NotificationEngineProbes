package org.notificationengine.probes;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;
import org.notificationengine.probes.configuration.ConfigurationReader;
import org.notificationengine.probes.constants.Constants;
import org.notificationengine.probes.domain.Channel;
import org.notificationengine.probes.probe.DatabaseProbe;
import org.notificationengine.probes.probe.FolderProbe;
import org.notificationengine.probes.probe.IProbe;
import org.notificationengine.probes.task.ProbeTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;
import java.util.Map;
import java.util.Timer;


public class Launcher {

    static Logger LOGGER = Logger.getLogger(Launcher.class);

    public static void main(String[] args) {

        LOGGER.info("Start the launcher");

        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});

        ConfigurationReader configurationReader = context.getBean(Constants.CONFIGURATION_READER, ConfigurationReader.class);

        Channel channel = configurationReader.readConfiguration();

        Timer timer = new Timer();

        String topicName = channel.getTopicName();

        IProbe probe = null;
        String probeType = null;

        LOGGER.debug("Channel with probeType " + channel.getProbeType());

        switch(channel.getProbeType()) {

            case Constants.PROBE_TYPE_FOLDER :

                LOGGER.debug("Detected Probe of type " + Constants.PROBE_TYPE_FOLDER);

                probeType = Constants.PROBE_TYPE_FOLDER;

                Map<String, Object> folderOptions = channel.getOptions();

                probe = new FolderProbe(topicName, folderOptions);

                break;

            case Constants.PROBE_TYPE_DATABASE :

                LOGGER.debug("Detected Probe of type " + Constants.PROBE_TYPE_DATABASE);

                probeType = Constants.PROBE_TYPE_DATABASE;

                Map<String, Object> databaseOptions = channel.getOptions();

                probe = new DatabaseProbe(topicName, databaseOptions);

                break;

            case Constants.PROBE_TYPE_CUSTOM :

                LOGGER.debug("Detected Probe of type " + Constants.PROBE_TYPE_CUSTOM);

                break;

        }

        LOGGER.debug("There is a scheduled operation");

        timer.schedule(new ProbeTask(probe), channel.getPeriod(), channel.getPeriod());

    }

}
