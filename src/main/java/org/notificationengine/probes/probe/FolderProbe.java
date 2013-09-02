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

import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class FolderProbe extends Probe{

    public static Logger LOGGER = Logger.getLogger(FolderProbe.class);

    Path pathToWatch;

    Collection<String> eventsToWatch;

    public FolderProbe() {
        super();
        this.eventsToWatch = new HashSet<>();
    }

    public FolderProbe(String topicName, Path pathToWatch, Collection<String> eventsToWatch) {

        super();

        this.setTopicName(topicName);

        this.setPathToWatch(pathToWatch);

        this.eventsToWatch = eventsToWatch;

        this.listen();

    }

    public FolderProbe(String topicName, String pathName, Collection<String> eventsToWatch) {

        super();

        LOGGER.info("FolderProbe instantiated with a pathToWatch in JSON format");

        this.setTopicName(topicName);

        this.pathToWatch = Paths.get(pathName);

        this.eventsToWatch = eventsToWatch;

        this.listen();

    }

    @Override
    public void listen() {

        try {
            WatchService watcher = this.pathToWatch.getFileSystem().newWatchService();

            Collection<WatchEvent.Kind<?>> eventsCollection = new HashSet<>();

            for(String event : eventsToWatch) {

                switch(event) {

                    case Constants.CREATED :
                        eventsCollection.add(StandardWatchEventKinds.ENTRY_CREATE);
                        break;

                    case Constants.DELETED :
                        eventsCollection.add(StandardWatchEventKinds.ENTRY_DELETE);
                        break;

                    case Constants.MODIFIED :
                        eventsCollection.add(StandardWatchEventKinds.ENTRY_MODIFY);
                        break;

                    default:
                        LOGGER.debug("Unknow event " + event + ", this is ignored");

                }
            }

            final int nbOfEventsToWatch = eventsCollection.size();

            //Convert in a array to be used by the Path::register method
            WatchEvent.Kind<?>[] eventsReadyToRegister = eventsCollection.toArray(new WatchEvent.Kind<?>[nbOfEventsToWatch]);

            this.pathToWatch.register(watcher, eventsReadyToRegister);

            while(Boolean.TRUE) {

                WatchKey watchKey = watcher.take();

                List<WatchEvent<?>> events = watchKey.pollEvents();

                for (WatchEvent event : events) {

                    JSONObject context = new JSONObject();

                    context.put(Constants.EVENT, event.kind().toString());

                    context.put(Constants.FILE_NAME, event.context().toString());

                    this.sendNotification();

                }

                //reset the key
                boolean valid = watchKey.reset();

                //exit loop if the key is not valid
                //e.g. if the directory was deleted
                if (!valid) {
                    break;
                }
            }

        } catch (IOException | InterruptedException e) {
            LOGGER.warn(ExceptionUtils.getFullStackTrace(e));
        }
    }

    @Override
    public void sendNotification() {

        JSONObject rawNotification = new JSONObject();

        rawNotification.put(Constants.TOPIC, this.getTopicName());

        JSONObject context = new JSONObject();

        context.put(Constants.SUBJECT, "Change in folder");

        context.put(Constants.CONTENT, this.getNotificationContext());

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

    public Path getPathToWatch() {
        return pathToWatch;
    }

    public void setPathToWatch(Path pathToWatch) {
        this.pathToWatch = pathToWatch;
    }

    public Collection<String> getEventsToWatch() {
        return eventsToWatch;
    }

    public void setEventsToWatch(Collection<String> eventsToWatch) {
        this.eventsToWatch = eventsToWatch;
    }

    public void addEventToWatch(String eventToWatch) {
        this.eventsToWatch.add(eventToWatch);
    }

    public void deleteEventToWatch(String eventNotToWatch) {
        this.eventsToWatch.remove(eventNotToWatch);
    }
}
