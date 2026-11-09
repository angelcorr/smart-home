package edu.smarthome.model;

import edu.smarthome.strategy.ClimateStrategy;

public interface ClimateControl extends Device {
  void setStrategy(ClimateStrategy strategy);
  ClimateStrategy getStrategy();

  void setCurrentTemperature(double celsius);
  double getCurrentTemperature();
  double getTargetTemperature();

  HvacMode getMode();
}
