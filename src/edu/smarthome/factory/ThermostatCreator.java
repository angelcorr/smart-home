package edu.smarthome.factory;

import edu.smarthome.model.Device;
import edu.smarthome.model.Thermostat;
import edu.smarthome.observer.EventBus;
import edu.smarthome.strategy.ComfortStrategy;

public class ThermostatCreator extends DeviceCreator {
  @Override
  protected String idPrefix() { return "THERMO"; }

  @Override
  protected Device createDevice(String id, String name, EventBus bus) {
    return new Thermostat(id, name, bus, new ComfortStrategy());
  }
}
