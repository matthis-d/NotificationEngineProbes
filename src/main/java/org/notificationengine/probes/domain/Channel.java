package org.notificationengine.probes.domain;

import java.util.Collection;
import java.util.HashSet;

public class Channel {

    private String id;

    private String topicName;

    private String probeType;

    private Collection<String> thingsToWatch;

    public Channel(String id) {

        super();

        this.id = id;

        this.thingsToWatch = new HashSet<>();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getProbeType() {
        return probeType;
    }

    public void setProbeType(String probeType) {
        this.probeType = probeType;
    }

    public Collection<String> getThingsToWatch() {
        return thingsToWatch;
    }

    public void setThingsToWatch(Collection<String> thingsToWatch) {
        this.thingsToWatch = thingsToWatch;
    }

    public void addThingToWatch(String thingToWatch) {
        this.thingsToWatch.add(thingToWatch);
    }

    public void removeThingToWatch(String thingNotToWatch) {
        this.thingsToWatch.remove(thingNotToWatch);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", topicName='" + topicName + '\'' +
                ", probeType='" + probeType + '\'' +
                ", thingsToWatch=" + thingsToWatch +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (!id.equals(channel.id)) return false;
        if (!probeType.equals(channel.probeType)) return false;
        if (thingsToWatch != null ? !thingsToWatch.equals(channel.thingsToWatch) : channel.thingsToWatch != null)
            return false;
        if (!topicName.equals(channel.topicName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + topicName.hashCode();
        result = 31 * result + probeType.hashCode();
        result = 31 * result + (thingsToWatch != null ? thingsToWatch.hashCode() : 0);
        return result;
    }
}
