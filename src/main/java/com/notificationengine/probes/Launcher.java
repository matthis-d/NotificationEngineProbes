package com.notificationengine.probes;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import com.notificationengine.probes.configuration.ConfigurationReader;
import com.notificationengine.probes.constants.Constants;
import com.notificationengine.probes.domain.Channel;
import com.notificationengine.probes.probe.DatabaseProbe;
import com.notificationengine.probes.probe.FolderProbe;
import com.notificationengine.probes.probe.IProbe;
import com.notificationengine.probes.task.ProbeTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Timer;


public class Launcher {

    static Logger LOGGER = Logger.getLogger(Launcher.class);

    private String contextName;

    public Launcher(String contextName) {

        super();
        this.contextName = contextName;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public void start() {

        LOGGER.info("Start the launcher");

        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {contextName});

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

                // get selector class
                String probeClass = (String) channel.getOption(Constants.PROBE_CLASS);

                LOGGER.debug("Detected probe class " + probeClass);

                // instantiate it
                try {
                    probeType = Constants.PROBE_TYPE_CUSTOM;

                    Class clazz = Class.forName(probeClass);

                    Constructor constructor = clazz.getConstructor(String.class, Map.class);

                    probe = (IProbe)constructor.newInstance(channel.getTopicName(), channel.getOptions());

                }
                catch(InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {

                    LOGGER.warn(ExceptionUtils.getFullStackTrace(e));

                    LOGGER.warn("Unable to instantiate class " + probeClass + ", channel will be ignored");

                }

                break;

        }

        LOGGER.debug("There is a scheduled operation");

        timer.schedule(new ProbeTask(probe), channel.getPeriod(), channel.getPeriod());

    }

}
