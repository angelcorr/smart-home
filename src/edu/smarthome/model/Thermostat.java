package edu.smarthome.model;

import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.observer.EventBus;
import edu.smarthome.strategy.ClimateStrategy;

import java.time.LocalTime;

public class Thermostat extends DeviceBase implements ClimateControl {
  private static final double TOLERANCE = 0.5;

  private ClimateStrategy strategy;
  private double currentTemperature = 20.0;
  private HvacMode mode = HvacMode.IDLE;

  public Thermostat(String id, String name, EventBus bus, ClimateStrategy strategy) {
    super(id, name, bus);
    this.strategy = strategy;
    this.on = true;
  }

  @Override
  public void turnOn() {
    super.turnOn();
    regulate(LocalTime.now());
  }

  @Override
  public void turnOff() {
    super.turnOff();
    mode = HvacMode.IDLE;
  }

  @Override
  public String getState() {
    return on ? mode + " " + currentTemperature + " °C (target " + getTargetTemperature() + ")" : "off";
  }

  public void setStrategy(ClimateStrategy newStrategy) {
    this.strategy = newStrategy;
    bus.publish(new DeviceChangedEvent(this, "strategy changed to " + newStrategy.getName()));
    regulate(LocalTime.now());
  }

  public void setCurrentTemperature(double temperature) {
    this.currentTemperature = temperature;
    regulate(LocalTime.now());
  }

  public void regulate(LocalTime now) {
    if (!on) return; // Early return when the thermostat is off
    double target = strategy.targetTemperature(now);
    HvacMode newMode;

    if (currentTemperature < target - TOLERANCE) {
      newMode = HvacMode.HEATING;
    } else if (currentTemperature > target + TOLERANCE) {
      newMode = HvacMode.COOLING;
    } else {
      newMode = HvacMode.IDLE;
    }

    if (newMode != mode) {
      mode = newMode;

      bus.publish(new DeviceChangedEvent(this, mode + "(current " + currentTemperature + ", target " + target + ")"));
    }
  }

  public ClimateStrategy getStrategy() {
    return strategy;
  }

  public double getCurrentTemperature() {
    return currentTemperature;
  }

  public double getTargetTemperature() {
    return strategy.targetTemperature(LocalTime.now());
  }

  public HvacMode getMode() {
    return mode;
  }
}
