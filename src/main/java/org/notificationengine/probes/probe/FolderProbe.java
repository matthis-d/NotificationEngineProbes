package org.notificationengine.probes.probe;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.notificationengine.probes.constants.Constants;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

public class FolderProbe extends Probe{

    public static Logger LOGGER = Logger.getLogger(FolderProbe.class);

    private Collection<File> pathsToWatch;

    public FolderProbe() {
        super();
    }

    public FolderProbe(String topicName, Map<String, Object> options) {

        super();

        LOGGER.info("FolderProbe instantiated with options");

        this.setTopicName(topicName);

        this.getAndSetPathsFromOptions(options);

        this.setNotificationSubject((String) options.get(Constants.SUBJECT));

        this.setLastTryTime(new Timestamp(new Date().getTime()));

        this.listen();

    }

    public void getAndSetPathsFromOptions(Map<String, Object> options) {

        this.pathsToWatch = new HashSet<>();

        String pathsAsJson = (String) options.get(Constants.PATHS);

        Object pathsAsObj = JSONValue.parse(pathsAsJson);

        JSONArray pathsJsonArray = (JSONArray)pathsAsObj;

        for(int i = 0; i<pathsJsonArray.size(); i++) {

            String path = (String)pathsJsonArray.get(i);

            this.pathsToWatch.add(new File(path));
        }

    }

    @Override
    public void listen() {

        Timestamp time = new Timestamp(new Date().getTime());

        for (File pathToWatch : this.pathsToWatch) {
        
	        for (File fileEntry : pathToWatch.listFiles()) {
	
	        	Path file = Paths.get(fileEntry.getAbsolutePath());
	        	
	        	BasicFileAttributes attrs;
				
	        	try {
					attrs = Files.readAttributes(file, BasicFileAttributes.class);
				} 
				catch (IOException e) {
					
					LOGGER.warn("Can not read attributes of file : " + fileEntry.getAbsolutePath());
					LOGGER.warn("File will be ignored");
					
					attrs = null;
				}
	        	
	        	if (attrs != null) {
	        		
		        	FileTime lastAccessTime = attrs.lastAccessTime();
		        	
		        	Long lastAccessTimeInMilliSeconds = lastAccessTime.toMillis();
		        	
		            if (lastAccessTimeInMilliSeconds.compareTo(this.getLastTryTime().getTime()) >= 0) {
		
		                JSONObject context = new JSONObject();
		
		                context.put(Constants.FILE_NAME, fileEntry.getName());
		
		                context.put(Constants.LAST_MODIFIED, fileEntry.lastModified());
		
		                this.setNotificationContext(context);
		
		                this.sendNotification();	
		            }
	        	}
	        }
        }

        this.setLastTryTime(time);

    }

    public Collection<File> getPathsToWatch() {
        return pathsToWatch;
    }

    public void setPathsToWatch(Collection<File> pathsToWatch) {
        this.pathsToWatch = pathsToWatch;
    }
}
