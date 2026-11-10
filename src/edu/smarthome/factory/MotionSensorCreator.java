package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.model.MotionSensor;
import edu.smarthome.observer.EventBus;

public class MotionSensorCreator extends DeviceCreator {

    @Override
    protected String idPrefix() {
        return "MOTION";
    }

    @Override
    protected Device createDevice(String id, String name, EventBus bus) {
        return new MotionSensor(id, name, bus);
    }
}
