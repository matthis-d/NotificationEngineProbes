package org.notificationengine.probes.probe;


import org.notificationengine.probes.constants.Constants;
import org.notificationengine.probes.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

public abstract class Probe implements IProbe{

    private String notificationMessage;

    private String topicName;

    private String serverUrl;

    public Probe() {

        Properties localSettingsProperties = (Properties) SpringUtils.getBean(Constants.LOCAL_SETTINGS_PROPERTIES);

        this.serverUrl = (String)localSettingsProperties.get(Constants.SERVER_URL);

        this.notificationMessage = "";
        this.topicName = "";

    }

    @Override
    public abstract void listen();

    @Override
    public abstract void sendNotification();

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
