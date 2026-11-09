package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.observer.DeviceAddedEvent;
import edu.smarthome.model.Room;
import edu.smarthome.observer.EventBus;

public abstract class DeviceCreator {
  private static int counter = 0;

  protected abstract Device createDevice(String id, String name, EventBus bus);
  protected abstract String idPrefix();

  public Device create(String name, Room room, EventBus bus) {
    String id = idPrefix() + "-" + (++counter);
    Device device = createDevice(id, name, bus);
    room.add(device);                                // Composite
    bus.publish(new DeviceAddedEvent(device, room)); // Observer
    return device;
  }
}
