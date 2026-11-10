package edu.smarthome.model;

import java.util.ArrayList;
import java.util.List;

public class Room extends DeviceGroup {
    private final List<Device> devices = new ArrayList<>();

    public Room(String name) {
        super("room-" + name.toLowerCase().replace(' ', '-'), name);
    }

    public List<Device> getDevices() {
        return getChildren();
    }

}
