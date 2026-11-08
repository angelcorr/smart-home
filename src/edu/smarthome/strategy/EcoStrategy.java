package edu.smarthome.strategy;

import java.time.LocalTime;

public class EcoStrategy implements ClimateStrategy {
  @Override
  public String getName() {
    return "Eco";
  }

  @Override
  public double targetTemperature(LocalTime now) {
    boolean night = now.getHour() >= 22 || now.getHour() < 6;
    return  night ? 17.0 : 20.0;
  }
}
