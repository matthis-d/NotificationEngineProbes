package com.notificationengine.probes.configuration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.notificationengine.probes.constants.Constants;
import com.notificationengine.probes.domain.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;


@Component(value = Constants.CONFIGURATION_READER)
public class ConfigurationReader {

    private static Logger LOGGER = Logger.getLogger(ConfigurationReader.class);

    private static String[] KNOWN_PROBE_TYPES = {Constants.PROBE_TYPE_FOLDER,
                                                 Constants.PROBE_TYPE_DATABASE,
                                                 Constants.PROBE_TYPE_CUSTOM};

    private static String[] KNOWN_CHANNEL_KEYS = {Constants.ID,
                                                  Constants.TOPIC,
                                                  Constants.PROBE_TYPE,
                                                  Constants.PERIOD};

    @Value("${config.directory}")
    private String configDirectory;


    public ConfigurationReader() {

        LOGGER.debug("ConfigurationReader instantiated");
    }

    public Channel readConfiguration() {

        Channel result = new Channel();

        try {
            LOGGER.info("Reading configuration file...");

            String configurationString = FileUtils.readFileToString(new File(this.configDirectory + System.getProperty("file.separator") + Constants.CONFIGURATION_FILE_NAME));

            LOGGER.debug("Configuration : " + configurationString);

            Object configurationObj = JSONValue.parse(configurationString);

            JSONObject configurationJsonObj = (JSONObject)configurationObj;

            String id = (String)configurationJsonObj.get(Constants.ID);

            if (StringUtils.isEmpty(id)) {

                LOGGER.warn("Found a channel without id, it will be ignored");
            }

            String topic = (String)configurationJsonObj.get(Constants.TOPIC);

            if (StringUtils.isEmpty(topic)) {

                LOGGER.warn("There is no topic, it will be ignored");
            }

            String probeType = (String)configurationJsonObj.get(Constants.PROBE_TYPE);

            if (StringUtils.isEmpty(probeType)) {

                LOGGER.warn("Found a channel without a probeType, it will be ignored");
            }

            // if selectorType is not of a known type or custom, ignore channel
            if (!this.isKnownProbeType(probeType)) {

                LOGGER.warn("Found a channel with an unknown selectorType [" + probeType + "], it will be ignored");
            }

            Integer period = (Integer)configurationJsonObj.get(Constants.PERIOD);

            if(period == null) {
                LOGGER.info("No period information found, set to default period");

                period = Constants.DEFAULT_PERIOD;
            }

            if(period == 0) {

                LOGGER.warn("Found a period of 0ms, will be set to default period");

                period = Constants.DEFAULT_PERIOD;
            }

            result.setId(id);
            result.setTopicName(topic);
            result.setProbeType(probeType);
            result.setPeriod(period);

            Set<String> keys = configurationJsonObj.keySet();
            for (String key : keys) {

                if (this.isChannelOption(key)) {

                    String value = configurationJsonObj.get(key).toString();

                    LOGGER.debug("Found option " + key + " : " + value);

                    result.addOption(key, value);
                }
            }


            LOGGER.debug("Found channel : " + result);

        }
        catch (IOException e) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(e));

            LOGGER.info("Configuration file not fully read.");
        }

        return result;
    }

    private boolean isChannelOption(String key) {

        return (!ArrayUtils.contains(KNOWN_CHANNEL_KEYS, key));
    }

    private boolean isKnownProbeType(String probeType) {

        return ArrayUtils.contains(KNOWN_PROBE_TYPES, probeType);
    }

    public String getConfigDirectory() {
        return configDirectory;
    }

    public void setConfigDirectory(String configDirectory) {
        this.configDirectory = configDirectory;
    }


}
