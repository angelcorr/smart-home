package edu.smarthome.model;

import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;

public class Television extends DeviceBase {
    private int channel;
    private int volume = 10;

    public Television(String id, String name, EventBus bus) {
        super(id, name, bus);
    }

    public int getChannel() { return channel; }

    public void changeChannel(int newChannel) {
        channel = newChannel;
        bus.publish(new DeviceChangedEvent(this, "channel " + channel));
    }

    public int getVolume() { return volume; }

    public void setVolume(int value) {
        volume = Math.max(0, Math.min(100, value));
        bus.publish(new DeviceChangedEvent(this, "volume " + volume));
    }

    @Override
    public String getState() {
        return on ? "on, channel " + channel + ", volume " + volume : "off";
    }
}
