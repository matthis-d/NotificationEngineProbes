package org.notificationengine.probes.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Channel {

    private String id;

    private String topicName;

    private String probeType;

    private Map<String, Object> options;

    public Channel(String id) {

        super();

        this.id = id;

        this.options = new HashMap<>();

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

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public void addOption(String key, Object value) {
        this.options.put(key, value);
    }

    public void removeOption(String key) {
        this.options.remove(key);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", topicName='" + topicName + '\'' +
                ", probeType='" + probeType + '\'' +
                ", options=" + options +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (!id.equals(channel.id)) return false;
        if (!probeType.equals(channel.probeType)) return false;
        if (options != null ? !options.equals(channel.options) : channel.options != null)
            return false;
        if (!topicName.equals(channel.topicName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + topicName.hashCode();
        result = 31 * result + probeType.hashCode();
        result = 31 * result + (options != null ? options.hashCode() : 0);
        return result;
    }
}
