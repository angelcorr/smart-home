package edu.smarthome.factory;

import edu.smarthome.model.Blind;
import edu.smarthome.model.Device;
import edu.smarthome.observer.EventBus;

public class BlindCreator extends DeviceCreator {
  @Override
  protected Device createDevice(String name, EventBus bus) {
    return new Blind(name, bus);
  }
}
