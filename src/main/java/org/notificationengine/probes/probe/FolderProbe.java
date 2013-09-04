package org.notificationengine.probes.probe;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.notificationengine.probes.constants.Constants;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class FolderProbe extends Probe{

    public static Logger LOGGER = Logger.getLogger(FolderProbe.class);

    File pathToWatch;


    public FolderProbe() {
        super();
    }

    public FolderProbe(String topicName, File pathToWatch) {

        super();

        this.setTopicName(topicName);

        this.setPathToWatch(pathToWatch);

        this.setLastTryTime(new Timestamp(new Date().getTime()));

        this.listen();

    }

    public FolderProbe(String topicName, Map<String, Object> options) {

        super();

        LOGGER.info("FolderProbe instantiated with options");

        this.setTopicName(topicName);

        this.getAndSetPathFromOptions(options);

        this.setNotificationSubject((String) options.get(Constants.SUBJECT));

        this.setLastTryTime(new Timestamp(new Date().getTime()));

        this.listen();

    }

    public void getAndSetPathFromOptions(Map<String, Object> options) {

        String pathName = (String) options.get(Constants.PATH);

        if(pathName != null && !StringUtils.isEmpty(pathName)) {

            this.pathToWatch = new File(pathName);
        }
        else {
            LOGGER.warn("The path is not valid");
        }

    }

    @Override
    public void listen() {

        Timestamp time = new Timestamp(new Date().getTime());

        for (File fileEntry : pathToWatch.listFiles()) {

            Timestamp lastModified = new Timestamp(fileEntry.lastModified());

            if(lastModified.after(this.getLastTryTime())) {

                JSONObject context = new JSONObject();

                context.put(Constants.FILE_NAME, fileEntry.getName());

                context.put(Constants.LAST_MODIFIED, fileEntry.lastModified());

                this.setNotificationContext(context);

                this.sendNotification();

            }
        }

        this.setLastTryTime(time);

    }

    public File getPathToWatch() {
        return pathToWatch;
    }

    public void setPathToWatch(File pathToWatch) {
        this.pathToWatch = pathToWatch;
    }
}
