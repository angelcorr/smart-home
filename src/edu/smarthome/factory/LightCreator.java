package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.model.Light;
import edu.smarthome.observer.EventBus;

public class LightCreator extends DeviceCreator {
  @Override
  protected Device createDevice(String name, EventBus bus) {
    return new Light(name, bus);
  }
}
