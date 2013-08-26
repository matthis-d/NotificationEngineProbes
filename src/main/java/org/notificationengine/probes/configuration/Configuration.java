package org.notificationengine.probes.configuration;


import org.notificationengine.probes.domain.Channel;

import java.util.Collection;
import java.util.HashSet;

public class Configuration {

    Collection<Channel> channels;

    public Configuration() {

        super();

        this.channels = new HashSet<>();
    }

    public Collection<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Collection<Channel> channels) {
        this.channels = channels;
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        this.channels.remove(channel);
    }

    public boolean hasChannelWithId(String id) {

        return (this.findChannelById(id) != null);
    }

    public Channel findChannelById(String id) {

        Channel result = null;

        for (Channel channel : channels) {

            if (channel.getId().equals(id)) {

                result = channel;

                break;
            }
        }

        return result;
    }

    public boolean hasChannelWithTopic(String topic) {

        return (this.findChannelByTopic(topic) != null);
    }

    public Channel findChannelByTopic(String topic) {

        Channel result = null;

        for (Channel channel : channels) {

            if (channel.getTopicName().equals(topic)) {

                result = channel;

                break;
            }
        }

        return result;
    }


}
