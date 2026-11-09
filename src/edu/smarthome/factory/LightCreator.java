package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.model.Light;
import edu.smarthome.observer.EventBus;

public class LightCreator extends DeviceCreator {
  @Override
  protected String idPrefix() { return "LIGHT"; }

  @Override
  protected Device createDevice(String id,String name, EventBus bus) {
    return new Light(id,name, bus);
  }
}
