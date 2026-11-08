package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.observer.DeviceAddedEvent;
import edu.smarthome.model.Room;
import edu.smarthome.observer.EventBus;

public abstract class DeviceCreator {

  protected abstract Device createDevice(String name, EventBus bus);

  // Shared workflow (uses the factory method)
  public Device create(String name, Room room, EventBus bus) {
    Device device = createDevice(name, bus);
    room.add(device);                                // Composite
    bus.publish(new DeviceAddedEvent(device, room)); // Observer
    return device;
  }
}
