package edu.smarthome.factory;

import edu.smarthome.adapter.LegacyThermostatAdapter;
import edu.smarthome.adapter.legacy.LegacyThermostat;
import edu.smarthome.model.Device;
import edu.smarthome.observer.EventBus;
import edu.smarthome.strategy.ComfortStrategy;

public class LegacyThermostatCreator extends DeviceCreator {
  @Override
  protected Device createDevice(String id, String name, EventBus bus) {
    return new LegacyThermostatAdapter(id, name, bus, new LegacyThermostat("LT-1000"), new ComfortStrategy());
  }

  @Override
  protected String idPrefix() {
    return "legacy-thermo";
  }
}
