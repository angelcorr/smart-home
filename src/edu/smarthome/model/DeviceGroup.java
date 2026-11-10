package edu.smarthome.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceGroup implements Device {
  private final String id;
  private final String name;
  private final List<Device> children = new ArrayList<>();

  public DeviceGroup(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public void add(Device device) {
    children.add(device);
  }

  public void remove(Device device) {
    children.remove(device);
  }

  public List<Device> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public List<Device> getLeaves() {
    List<Device> leaves = new ArrayList<>();
    for (Device d : children) {
      if (d instanceof  DeviceGroup g) {
        leaves.addAll(g.getLeaves());
      } else {
        leaves.add(d);
      }
    }

    return leaves;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void turnOn() {
    children.forEach(Device::turnOn);
  }

  @Override
  public void turnOff() {
    children.forEach(Device::turnOff);
  }

  @Override
  public boolean isOn() {
    return children.stream().anyMatch(Device::isOn);
  }

  @Override
  public String getState() {
    List<Device> leaves = getLeaves();
    long on = leaves.stream().filter(Device::isOn).count();
    return on + " of " + leaves.size() + " on.";
  }

  @Override
  public String toString() {
    return name;
  }
}
