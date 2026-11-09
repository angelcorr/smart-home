package edu.smarthome.observer;

import edu.smarthome.model.Device;
import edu.smarthome.model.Room;

public final class DeviceAddedEvent {
  private final Device device;
  private final Room room;

  public DeviceAddedEvent(Device device, Room room) {
    this.device = device;
    this.room = room;
  }

  public Device device() {
    return device;
  }

  public Room room() {
    return room;
  }

  @Override
  public String toString() {
    return "DeviceAddedEvent[device=" + device.getName()
        + ", room=" + room.getName() + "]";
  }
}
