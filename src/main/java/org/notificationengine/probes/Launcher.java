package org.notificationengine.probes;

import org.apache.log4j.Logger;
import org.notificationengine.probes.configuration.Configuration;
import org.notificationengine.probes.configuration.ConfigurationReader;
import org.notificationengine.probes.constants.Constants;
import org.notificationengine.probes.domain.Channel;
import org.notificationengine.probes.probe.DatabaseProbe;
import org.notificationengine.probes.probe.FolderProbe;
import org.notificationengine.probes.probe.IProbe;
import org.notificationengine.probes.task.ProbeTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Timer;


public class Launcher {

    static Logger LOGGER = Logger.getLogger(Launcher.class);

    public static void main(String[] args) {

        LOGGER.info("Start the launcher");

        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});

        ConfigurationReader configurationReader = context.getBean(Constants.CONFIGURATION_READER, ConfigurationReader.class);

        Configuration configuration = configurationReader.readConfiguration();

        Timer timer = new Timer();

        LOGGER.debug("Number of channels in main : " + configuration.getChannels().size());

        for (Channel channel : configuration.getChannels()) {

            String topicName = channel.getTopicName();

            IProbe probe = null;
            String probeType = null;

            LOGGER.debug("Channel with probeType " + channel.getProbeType());

            switch(channel.getProbeType()) {

                case Constants.PROBE_TYPE_FOLDER :

                    LOGGER.debug("Detected Probe of type " + Constants.PROBE_TYPE_FOLDER);

                    probeType = Constants.PROBE_TYPE_FOLDER;

                    Map<String, Object> folderOptions = channel.getOptions();

                    String directories = (String)folderOptions.get(Constants.DIRECTORIES);

                    if(directories != null) {
                        probe = new FolderProbe(topicName, directories);
                    }
                    else {
                        LOGGER.warn("There was no directories set in the configuration, probe has not been instantiated");
                    }

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

            if(probeType.equals(Constants.PROBE_TYPE_DATABASE)) {

                LOGGER.debug("There is a scheduled operation");

                timer.schedule(new ProbeTask(probe), 10000, 10000);

            }
        }

    }

}
