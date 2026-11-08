package edu.smarthome.strategy;

import java.time.LocalTime;

public class ComfortStrategy implements ClimateStrategy {
  @Override
  public String getName() {
    return "Comfort";
  }

  @Override
  public double targetTemperature(LocalTime now) {
    return 22.0;
  }
}
