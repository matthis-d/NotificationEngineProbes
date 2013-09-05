package com.notificationengine.probes.domain;

import java.util.HashMap;
import java.util.Map;

public class Channel {

    private String id;

    private String topicName;

    private String probeType;

    private Integer period;

    private Map<String, Object> options;

    public Channel() {
        super();
        this.options = new HashMap<>();
    }

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

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
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

    public Object getOption(String key) {
        return this.options.get(key);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("id='").append(id).append('\'');
        sb.append(", topicName='").append(topicName).append('\'');
        sb.append(", probeType='").append(probeType).append('\'');
        sb.append(", period=").append(period);
        sb.append(", options=").append(options);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (!id.equals(channel.id)) return false;
        if (options != null ? !options.equals(channel.options) : channel.options != null) return false;
        if (period != null ? !period.equals(channel.period) : channel.period != null) return false;
        if (!probeType.equals(channel.probeType)) return false;
        if (!topicName.equals(channel.topicName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + topicName.hashCode();
        result = 31 * result + probeType.hashCode();
        result = 31 * result + (period != null ? period.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        return result;
    }
}
