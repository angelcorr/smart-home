package edu.smarthome.factory;

import edu.smarthome.model.DoorLock;
import edu.smarthome.model.Device;
import edu.smarthome.observer.EventBus;

public class DoorLockCreator extends DeviceCreator {

    @Override
    protected String idPrefix() {
        return "LOCK";
    }

    @Override
    protected Device createDevice(String id, String name, EventBus bus) {
        return new DoorLock(id, name, bus);
    }
}
