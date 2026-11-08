package edu.smarthome.model;

import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;

public class Blind implements Device {
    private final String name;
    private final EventBus bus;
    private boolean on;

    public Blind(String name, EventBus bus) {
        this.name = name;
        this.bus = bus;
    }

    public void turnOn() {
        on = true;
        bus.publish(new DeviceChangedEvent(this, "turned on"));
    }

    public void turnOff() {
        on = false;
        bus.publish(new DeviceChangedEvent(this, "turned off"));
    }

    public boolean isOn() { return on; }

    @Override
    public String getName() { return name; }
}
