package com.notificationengine.probes.custom.filecontentprobe;

import com.notificationengine.probes.custom.constants.CustomConstants;
import com.notificationengine.probes.probe.Probe;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class FileContentProbe extends Probe{

    public static Logger LOGGER = Logger.getLogger(FileContentProbe.class);

    private File fileToWatch;

    public FileContentProbe(String topicName, Map<String, Object> options) {

        super();

        this.setTopicName(topicName);

        String filePath = (String) options.get(CustomConstants.FILE_PATH);

        if(filePath == null && !StringUtils.isEmpty(filePath)) {

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

                this.setNotificationContext(context);

                this.sendNotification();

            }
        }
        else {

            LOGGER.warn("File set is not a valid file, no notification can be sent");
        }

        this.setLastTryTime(time);

    }

    public File getFileToWatch() {
        return fileToWatch;
    }

    public void setFileToWatch(File fileToWatch) {
        this.fileToWatch = fileToWatch;
    }
}
