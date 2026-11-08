package edu.smarthome.model;

import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;

public class Light extends DeviceBase {
    private int brightness;

    public Light(String id, String name, EventBus bus) {
        super(id, name, bus);
    }

    public int getBrightness() { return brightness; }

    public void setBrightness(int value) {
        brightness = Math.max(0, Math.min(100, value));
        bus.publish(new DeviceChangedEvent(this, "brightness " + brightness));
    }
}