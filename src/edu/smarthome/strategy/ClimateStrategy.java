package edu.smarthome.strategy;

import java.time.LocalTime;

public interface ClimateStrategy {
  String getName();
  double targetTemperature(LocalTime now);
}
