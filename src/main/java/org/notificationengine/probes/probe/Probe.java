package org.notificationengine.probes.probe;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.notificationengine.probes.constants.Constants;
import org.notificationengine.probes.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.Properties;

public abstract class Probe implements IProbe{

    public static Logger LOGGER = Logger.getLogger(Probe.class);

    private JSONObject notificationContext;

    private String notificationSubject;

    private String topicName;

    private String serverUrl;

    private Timestamp lastTryTime;

    public Probe() {

        Properties localSettingsProperties = (Properties) SpringUtils.getBean(Constants.LOCAL_SETTINGS_PROPERTIES);

        this.serverUrl = (String)localSettingsProperties.get(Constants.SERVER_URL);

        this.notificationContext = new JSONObject();
        this.topicName = "";
        this.notificationSubject = "";

    }

    @Override
    public abstract void listen();

    @Override
    public void sendNotification() {

        JSONObject rawNotification = new JSONObject();

        rawNotification.put(Constants.TOPIC, this.getTopicName());

        JSONObject context = this.getNotificationContext();

        context.put(Constants.SUBJECT, this.getNotificationSubject());

        rawNotification.put(Constants.CONTEXT, context);

        String url = this.getServerUrl() + Constants.RAW_NOTIFICATION_SIMPLE_POST_URL;

        LOGGER.debug(url);

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(url);

        try {
            StringEntity params = new StringEntity(rawNotification.toString());

            post.setEntity(params);

            post.addHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(post);

            LOGGER.info("Notification sent : " + rawNotification.toString());
        }
        catch(Exception ex) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            
            LOGGER.info("Notification not sent : " + rawNotification.toString());
        }
        finally {
            client.getConnectionManager().shutdown();
        }
    }

    public JSONObject getNotificationContext() {
        return notificationContext;
    }

    public void setNotificationContext(JSONObject notificationContext) {
        this.notificationContext = notificationContext;
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

    public String getNotificationSubject() {
        return notificationSubject;
    }

    public void setNotificationSubject(String notificationSubject) {
        this.notificationSubject = notificationSubject;
    }

    public Timestamp getLastTryTime() {
        return lastTryTime;
    }

    public void setLastTryTime(Timestamp lastTryTime) {
        this.lastTryTime = lastTryTime;
    }
}
