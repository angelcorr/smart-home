package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.model.Television;
import edu.smarthome.observer.EventBus;

public class TelevisionCreator extends DeviceCreator {
  @Override
  protected Device createDevice(String name, EventBus bus) {
    return new Television(name, bus);
  }
}
