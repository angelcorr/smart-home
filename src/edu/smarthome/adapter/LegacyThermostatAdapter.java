package edu.smarthome.adapter;

import edu.smarthome.adapter.legacy.LegacyThermostat;
import edu.smarthome.model.ClimateControl;
import edu.smarthome.model.DeviceBase;
import edu.smarthome.model.HvacMode;
import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;
import edu.smarthome.strategy.ClimateStrategy;

import java.time.LocalTime;
import java.util.Objects;

public class LegacyThermostatAdapter extends DeviceBase implements ClimateControl {
  private final LegacyThermostat legacy; // adaptee
  private ClimateStrategy strategy;
  private HvacMode mode = HvacMode.IDLE;

  public LegacyThermostatAdapter(String id, String name, EventBus bus, LegacyThermostat legacy, ClimateStrategy strategy) {
    super(id, name, bus);
    this.legacy = legacy;
    this.strategy = strategy;
    this.on = true;
  }

  @Override
  public void turnOn() {
    super.turnOn();
    regulate();
  }

  @Override
  public void turnOff() {
    super.turnOff();
    mode = HvacMode.IDLE;
  }

  @Override
  public String getState() {
    return on ? mode + " " + getCurrentTemperature() + "°C (target " + getTargetTemperature() + ")" : "off";
  }

  @Override
  public void setStrategy(ClimateStrategy strategy) {
    this.strategy = Objects.requireNonNull(strategy);
    bus.publish(new DeviceChangedEvent(this, "strategy changed to " + strategy.getName()));
    regulate();
  }

  @Override
  public ClimateStrategy getStrategy() {
    return strategy;
  }

  @Override
  public void setCurrentTemperature(double celsius) {
    legacy.setCurrentFahrenheit(toFahrenheit(celsius));
    regulate();
  }

  @Override
  public double getCurrentTemperature() {
    return toCelsius(legacy.readTemperatureFahrenheit());
  }

  @Override
  public double getTargetTemperature() {
    return strategy.targetTemperature(LocalTime.now());
  }

  @Override
  public HvacMode getMode() {
    return mode;
  }

  // Adapter logic
  private void regulate() {
    if (!on) return; // Early return if the thermostat is off

    double target = strategy.targetTemperature(LocalTime.now());
    legacy.setTargetFahrenheit(toFahrenheit(target));

    HvacMode newMode = toMode(legacy.getStatusCode());

    if (newMode != mode) {
      mode = newMode;
      bus.publish(new DeviceChangedEvent(this, mode + " (current " + getCurrentTemperature() + ", target " + target + ")"));
    }
  }

  // Transformation methods
  private static int toFahrenheit(double celsius) {
    return (int) Math.round(celsius * 9 / 5 + 32);
  }

  private static double toCelsius(int fahrenheit) {
    return Math.round((fahrenheit - 32) * 5.0 / 9 * 10 / 10.0);
  }

  private static HvacMode toMode(int code) {
    return switch (code) {
      case 1 -> HvacMode.HEATING;
      case 2 -> HvacMode.COOLING;
      default -> HvacMode.IDLE;
    };
  }
}
