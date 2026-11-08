package edu.smarthome.model;

import edu.smarthome.model.Device;

public final class DeviceChangedEvent {
    private final Device device;
    private final String description;

    public DeviceChangedEvent(Device device, String description) {
        this.device = device;
        this.description = description;
    }

    public Device device() { return device; }
    public String description() { return description; }

    @Override
    public String toString() {
        return "DeviceChangedEvent[device=" + device.getName()
                + ", description=" + description + "]";
    }
}

