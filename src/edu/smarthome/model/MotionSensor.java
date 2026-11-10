package edu.smarthome.model;

import edu.smarthome.observer.EventBus;
import edu.smarthome.observer.MotionDetectedEvent;

public class MotionSensor extends DeviceBase {

    public MotionSensor(String id, String name, EventBus bus) {
        super(id, name, bus);
        this.on = true;
    }

    public void detectMotion() {
        if (!on) return;
        bus.publish(new MotionDetectedEvent(this));
    }

    @Override
    public String getState() {
        if (on) {
            return "watching";
        } else {
            return "off";
        }
    }
}
