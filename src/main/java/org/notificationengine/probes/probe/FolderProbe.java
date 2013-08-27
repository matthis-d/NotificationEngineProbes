package org.notificationengine.probes.probe;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.notificationengine.probes.constants.Constants;

import java.nio.file.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class FolderProbe extends Probe{

    public static Logger LOGGER = Logger.getLogger(FolderProbe.class);

    Collection<Path> pathsToWatch;

    public FolderProbe() {
        super();
        this.pathsToWatch = new HashSet<>();
    }

    public FolderProbe(String topicName, Collection<Path> pathsToWatch) {

        super();

        this.setTopicName(topicName);
        this.setPathsToWatch(pathsToWatch);

        this.listen();

    }

    public FolderProbe(String topicName, String pathsToWatchJson) {

        super();

        LOGGER.info("FolderProbe instantiated with a pathToWatch in JSON format");

        this.pathsToWatch = new HashSet<>();

        this.setTopicName(topicName);
        this.readPathsFromString(pathsToWatchJson);

        this.listen();

    }

    public void readPathsFromString(String listOfPathsString) {

        LOGGER.debug("readPathsFromString");

        Object listOfPathsObj = JSONValue.parse(listOfPathsString);

        JSONArray listOfPathsJsonObj = (JSONArray)listOfPathsObj;

        for(int i = 0; i<listOfPathsJsonObj.size(); i++) {

            JSONObject path = (JSONObject)listOfPathsJsonObj.get(i);

            String pathName = (String)path.get(Constants.PATH);

            LOGGER.debug("Path found: " + pathName);

            Path pathObject = Paths.get(pathName);

            this.addPathToWatch(pathObject);

        }

    }

    @Override
    public void listen() {

        for(Path path : this.pathsToWatch) {
            while(Boolean.TRUE) {
                try {
                    WatchService watcher = path.getFileSystem().newWatchService();
                    path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

                    WatchKey watchKey = watcher.take();

                    List<WatchEvent<?>> events = watchKey.pollEvents();

                    for (WatchEvent event : events) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {

                            this.setNotificationMessage("Created: " + event.context().toString());

                            LOGGER.info("Created: " + event.context().toString());

                            this.sendNotification();
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            this.setNotificationMessage("Delete: " + event.context().toString());

                            LOGGER.info("Delete: " + event.context().toString());

                            this.sendNotification();
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            this.setNotificationMessage("Modify: " + event.context().toString());

                            LOGGER.info("Modify: " + event.context().toString());

                            this.sendNotification();
                        }

                    }

                } catch (Exception e) {
                    LOGGER.warn("Error: " + e.toString());
                }
            }
        }
    }

    @Override
    public void sendNotification() {

        JSONObject rawNotification = new JSONObject();

        rawNotification.put(Constants.TOPIC, this.getTopicName());

        JSONObject context = new JSONObject();

        context.put(Constants.SUBJECT, "Change in folder");

        context.put(Constants.CONTENT, this.getNotificationMessage());

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

        }
        catch(Exception ex) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));

        }
        finally {
            client.getConnectionManager().shutdown();
        }

    }

    public Collection<Path> getPathsToWatch() {
        return pathsToWatch;
    }

    public void setPathsToWatch(Collection<Path> pathsToWatch) {
        this.pathsToWatch = pathsToWatch;
    }

    public void addPathToWatch(Path pathToWatch) {
        this.pathsToWatch.add(pathToWatch);
    }

    public void removePathToWatch(Path pathNotToWatch) {
        this.pathsToWatch.remove(pathNotToWatch);
    }
}
