package com.notificationengine.probes.custom.filecontentprobe;

import com.notificationengine.probes.constants.Constants;
import com.notificationengine.probes.custom.constants.CustomConstants;
import com.notificationengine.probes.probe.Probe;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class FileContentProbe extends Probe{

    public static Logger LOGGER = Logger.getLogger(FileContentProbe.class);

    private File fileToWatch;

    public FileContentProbe(String topicName, Map<String, Object> options) {

        super();

        this.setTopicName(topicName);

        String filePath = (String) options.get(CustomConstants.FILE_PATH);

        LOGGER.info("File path: " + filePath);

        if(!StringUtils.isEmpty(filePath)) {

            this.fileToWatch = new File(filePath);

        }
        else {

            LOGGER.warn("No valid filePath set, the probe will not work");

            this.fileToWatch = new File("/");
        }

    }

    @Override
    public void listen() {

        Timestamp time = new Timestamp(new Date().getTime());

        if(this.fileToWatch.isFile()) {

            Timestamp lastModified = new Timestamp(this.fileToWatch.lastModified());

            if(lastModified.after(this.getLastTryTime())) {

                JSONObject context = new JSONObject();

                String fileContent = "";

                try {

                     fileContent = FileUtils.readFileToString(this.fileToWatch, "UTF-8");
                }
                catch(IOException ex) {
                    LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                }

                context.put(CustomConstants.FILE_CONTENT, fileContent);

                this.setNotificationSubject("File content modified");

                this.setNotificationContext(context);

                this.sendNotification();

            }
        }
        else {

            LOGGER.warn("File set is not a valid file, no notification can be sent");
        }

        this.setLastTryTime(time);

    }

    @Override
    public void sendNotification() {

        String url = this.getServerUrl() + CustomConstants.RAW_NOTIFICATION_WITH_ATTACH_POST_URL;

        LOGGER.debug(url);

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nameValuePairs = this.listNameValuePairs();

        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int index=0; index < nameValuePairs.size(); index++) {
                if(nameValuePairs.get(index).getName().equalsIgnoreCase(CustomConstants.FILES_ARRAY)) {
                    // If the key equals to "image", we use FileBody to transfer the data
                    entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
                } else {
                    // Normal string data
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                }
            }

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Notification sent" + this.getNotificationContext().toString());

    }

    public List<NameValuePair> listNameValuePairs() {

        List<NameValuePair> result = new ArrayList<>();

        JSONObject rawNotification = new JSONObject();

        rawNotification.put(Constants.TOPIC, this.getTopicName());

        JSONObject context = this.getNotificationContext();

        context.put(Constants.SUBJECT, this.getNotificationSubject());

        rawNotification.put(Constants.CONTEXT, context);

        NameValuePair nameValuePairJson = new NameValuePair(CustomConstants.JSON, rawNotification.toString());

        NameValuePair nameValuePairFile = new NameValuePair(CustomConstants.FILES_ARRAY, this.fileToWatch.getPath());

        result.add(nameValuePairJson);

        result.add(nameValuePairFile);

        return result;
    }

    public File getFileToWatch() {
        return fileToWatch;
    }

    public void setFileToWatch(File fileToWatch) {
        this.fileToWatch = fileToWatch;
    }
}
