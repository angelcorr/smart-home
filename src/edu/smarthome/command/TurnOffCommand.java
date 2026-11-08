package edu.smarthome.command;

import edu.smarthome.model.Device;

public class TurnOffCommand implements Command {
    private final Device device;
    private boolean wasOn;

    public TurnOffCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        wasOn = device.isOn();
        device.turnOff();
    }

    @Override
    public void undo() {
        if (wasOn) {
            device.turnOn();
        } else {
            device.turnOff();
        }
    }
}