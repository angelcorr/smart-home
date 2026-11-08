package edu.smarthome.factory;

import java.util.EnumMap;
import java.util.Map;

public class DeviceCreatorRegistry {
  private final Map<DeviceType, DeviceCreator> creators = new EnumMap<>(DeviceType.class);

  public DeviceCreatorRegistry() {
    creators.put(DeviceType.LIGHT, new LightCreator());
    creators.put(DeviceType.TELEVISION, new TelevisionCreator());
    creators.put(DeviceType.BLIND, new BlindCreator());
    creators.put(DeviceType.THERMOSTAT, new ThermostatCreator());
  }

  public DeviceCreator get(DeviceType type) {
    return creators.get(type);
  }
}
