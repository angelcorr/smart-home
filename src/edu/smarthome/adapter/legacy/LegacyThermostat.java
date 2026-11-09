package edu.smarthome.adapter.legacy;

public class LegacyThermostat {
  private final String serial;
  private int currentF = 68;
  private int targetF = 68;

  public LegacyThermostat(String serial) {
    this.serial = serial;
  }

  public String getSerial() {
    return serial;
  }

  public int readTemperatureFahrenheit() {
    return currentF;
  }

  public void setCurrentFahrenheit(int currentF) {
    this.currentF = currentF;
  }

  public void setTargetFahrenheit(int targetF) {
    this.targetF = targetF;
  }

  public int getStatusCode() {
    if (currentF < targetF - 1) {
      return 1;
    }

    if (currentF > targetF + 1) {
      return 2;
    }

    return 0;
  }
}
