package edu.smarthome.model;

import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;

public abstract class DeviceBase implements Device {
    private final String id;
    private final String name;
    protected final EventBus bus;
    protected boolean on;

    protected DeviceBase(String id, String name, EventBus bus) {
        this.id = id;
        this.name = name;
        this.bus = bus;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public void turnOn() {
        on = true;
        bus.publish(new DeviceChangedEvent(this, "turned on"));
    }

    @Override
    public void turnOff() {
        on = false;
        bus.publish(new DeviceChangedEvent(this, "turned off"));
    }

    @Override
    public boolean isOn() { return on; }

    @Override
    public String getState() { return on ? "on" : "off"; }
}