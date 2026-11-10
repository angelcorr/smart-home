package edu.smarthome.observer;

import edu.smarthome.model.MotionSensor;

public final class MotionDetectedEvent {
    private final MotionSensor sensor;

    public MotionDetectedEvent(MotionSensor sensor) {
        this.sensor = sensor;
    }

    public MotionSensor sensor() {
        return sensor;
    }
}
