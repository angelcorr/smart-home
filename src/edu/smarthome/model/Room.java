package edu.smarthome.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String name;
    private final List<Device> devices = new ArrayList<>();

    public Room(String name) { this.name = name; }

    public void add(Device device) { devices.add(device); }
    public List<Device> getDevices() { return devices; }
    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}
