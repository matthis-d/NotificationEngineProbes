# Notification Engine Probes

## 1. What is it for ? 

This is a Java application (JAR) to launch with a configuration file that aims to scan a folder, a database or whatever you want to in order to send notifications through the [Notification Engine](https://github.com/rgirodon/NotificationEngine). 

## 2. What is built in it ?

In this project, you'll find two built-in probes but you can still customize it for your needs (see part 3). 

### 2.1. Folder probe

You can install the JAR in order to scan a specific folder. You have to install as many probes as there are folders to scan. You can choose the topic of each notification and you have to name the folder you want to scan.
 
All of this configuration have to put in a folder named ```configuration``` at the same JAR's level. In this folder, you have to add a file named ```configuration.json``` and it has to have a structure like this : 
```JSON
{
    "id": "facturationChannel",
    "topic": "facturation.societe2",
    "probeType": "folderProbe",
    "path": "/Users/Matthis/Desktop",
    "period": 10000,
    "subject": "File created or modified"
}
```

Each time a file is added, modified or deleted, a notification is sent to the notification engine server's URL with this format : 
```JSON
{
    "topic": "facturation.societe2",
    "context": {
        "lastModified": "1378308741",
        "fileName" : "file-modified.txt"
    }
}
```

### 2.2. Database probe

You can also launch the JAR in order to scan a database. Each 10 seconds (not configurable at the moment) , the probe will execute the queries you set in the configuration file and send a notification for each row found. It is advised to use a query with a timestamp in order to get last updates or last entries in the database. 

At the moment, the database has to be MySQL.

The configuration should be like the following one : 
```JSON
{
    "id" : "helpdeskChannel",
    "topic": "helpdesk.societe1",
    "probeType" : "databaseProbe",
    "user": "root",
    "password" : "root",
    "url" : "jdbc:mysql://localhost:3306/notification-engine",
    "driverClassName": "com.mysql.jdbc.Driver",
    "queries" : [
        {"getLastUpdates" : "SELECT t.id, t.name, t.message FROM tableToWatch t WHERE t.updated_at > ?"}
    ]
}
```

For each row in the result, you will get a notification with the topic in the configuration and the context will be all column names selected with the result. In our example case, it would be : 
```JSON
{
    "topic" : "helpdesk.societe1",
    "context": {
        "id": "42",
        "name": "This is the name",
        "message" : "this is the message"
    }
}
```


## 3. How to customize it ?

Because the project uses Maven, our project can be extended with an overlay. 

We provide an interface for the probe which is the following : 
```JAVA
public interface IProbe {

    public void listen();

    public void sendNotification();

}
```

And we also built an abstract class named ```Probe``` that implements this interface and add 3 properties with their getters and setters : 
- the notificationContext : a JSONObject (from JSON Simple) that store the context of the notification to send
- the topicName : a String which contains the topic of the notification
- the serverUrl : a String that stores the URL of the notification engine server. This URL is stetted in the ```localsettings.properties``` file in the resources folder.

If you want to use other databases (others than MySQL), you have to add their drivers in the ```pom.xml``` file.   


## 4. How to install it ? 

First of all, you need to have Java 7 installed on your machine to make it run. 

You have to create ```localsettings.properties```and ```configuration.json``` files and to place it in a folder named ```configuration```. 
```localsettings.properties``` should look like this : 
```ini
config.directory=/Applications/eclipse/workspace/NotificationEngineProbes/configuration
server.url=http://localhost:8080/notificationengine-0.0.1-SNAPSHOT/
```
The ```config.directory``` property is where the configuration file is and the ```server.url``` property specifies what the url of the notification engine server is.

Once everything is configured, you just have to run : 
```
java -jar NotificationEngineProbes-0.0.1-SNAPSHOT.jar -cp .
```
where the JAR is the executable archive of the project packaged by Maven. 

