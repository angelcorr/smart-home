package edu.smarthome.model;

import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;

public class Blind extends DeviceBase {
    private int opening;

    public Blind (String id, String name, EventBus bus) {
        super(id, name, bus);
    }

    public int getOpening() { return opening; }

    public void setOpening(int value) {
        opening = Math.max(0, Math.min(100, value));
        bus.publish(new DeviceChangedEvent(this, "opening " + opening));
    }

    @Override
    public String getState() {
        return on ? "open " + opening + "%" : "closed";
    }
}