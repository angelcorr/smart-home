package edu.smarthome.model;

import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;

// In this class "on" means LOCKED
public class DoorLock extends DeviceBase {

    public DoorLock(String id, String name, EventBus bus) {
        super(id, name, bus);
    }

    public void lock() {
        if (on) return;
        on = true;
        bus.publish(new DeviceChangedEvent(this, "locked"));
    }

    public void unlock() {
        if (!on) return;
        on = false;
        bus.publish(new DeviceChangedEvent(this, "unlocked"));
    }

    @Override
    public String getState() {
        if (on) {
            return "locked";
        } else {
            return "unlocked";
        }
    }
}
