package edu.smarthome.strategy;

import java.time.LocalTime;

public class AwayStrategy implements ClimateStrategy {
  @Override
  public String getName() {
    return "Away";
  }

  @Override
  public double targetTemperature(LocalTime now) {
    return 15.0;
  }
}
