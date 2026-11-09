package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.model.Television;
import edu.smarthome.observer.EventBus;

public class TelevisionCreator extends DeviceCreator {
  @Override
  protected String idPrefix() { return "TV"; }

  @Override
  protected Device createDevice(String id,String name, EventBus bus) {
    return new Television(id,name, bus);
  }
}
