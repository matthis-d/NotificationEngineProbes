package org.notificationengine.probes.configuration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.notificationengine.probes.constants.Constants;
import org.notificationengine.probes.domain.Channel;
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
                                                  Constants.PROBE_TYPE};

    @Value("${config.directory}")
    private String configDirectory;


    public ConfigurationReader() {

        LOGGER.debug("ConfigurationReader instantiated");
    }

    public Configuration readConfiguration() {

        Configuration result = new Configuration();


        try {
            LOGGER.info("Reading configuration file...");

            String configurationString = FileUtils.readFileToString(new File(this.configDirectory + System.getProperty("file.separator") + Constants.CONFIGURATION_FILE_NAME));

            LOGGER.debug("Configuration : " + configurationString);

            Object configurationObj = JSONValue.parse(configurationString);

            JSONObject configurationJsonObj = (JSONObject)configurationObj;

            JSONArray channelsArray = (JSONArray)configurationJsonObj.get(Constants.CHANNELS);

            LOGGER.debug("Nbr of channels : " + channelsArray.size());

            for (int i = 0; i < channelsArray.size(); i++) {

                JSONObject channelJsonObj = (JSONObject)channelsArray.get(i);

                String id = (String)channelJsonObj.get(Constants.ID);

                if (StringUtils.isEmpty(id)) {

                    LOGGER.warn("Found a channel without id, it will be ignored");
                    continue;
                }

                if (result.hasChannelWithId(id)) {

                    LOGGER.warn("Found duplicate channel id [" + id + "], it will be ignored");
                    continue;
                }

                String topic = (String)channelJsonObj.get(Constants.TOPIC);

                if (StringUtils.isEmpty(topic)) {

                    LOGGER.warn("Found a channel without topic, it will be ignored");
                    continue;
                }

                if (result.hasChannelWithTopic(topic)) {

                    LOGGER.warn("Found multiple channels for same topic [" + topic + "], it will be ignored");
                    continue;
                }

                String probeType = (String)channelJsonObj.get(Constants.PROBE_TYPE);

                if (StringUtils.isEmpty(probeType)) {

                    LOGGER.warn("Found a channel without a probeType, it will be ignored");
                    continue;
                }

                // if selectorType is not of a known type or custom, ignore channel
                if (!this.isKnownProbeType(probeType)) {

                    LOGGER.warn("Found a channel with an unknown selectorType [" + probeType + "], it will be ignored");
                    continue;
                }



                Channel channel = new Channel(id);
                channel.setTopicName(topic);
                channel.setProbeType(probeType);

                Set<String> keys = channelJsonObj.keySet();
                for (String key : keys) {

                    if (this.isChannelOption(key)) {

                        String value = channelJsonObj.get(key).toString();

                        LOGGER.debug("Found option " + key + " : " + value);

                        channel.addOption(key, value);
                    }
                }


                LOGGER.debug("Found channel : " + channel);

                result.addChannel(channel);
            }

            LOGGER.info("Configuration file read.");
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
