package edu.smarthome.model;

public interface Device {
    String getId();
    String getName();
    void turnOn();
    void turnOff();
    boolean isOn();
    String getState();
}
