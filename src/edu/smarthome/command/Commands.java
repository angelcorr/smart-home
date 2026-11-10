package edu.smarthome.command;

import edu.smarthome.model.Device;
import edu.smarthome.model.DeviceGroup;

import java.util.ArrayList;
import java.util.List;

public class Commands {
  private Commands() {}

  public static Command turnOn(Device target) {
    return build(target, true);
  }

  public static Command turnOff(Device target) {
    return build(target, false);
  }

  private static Command build(Device target, boolean on) {
    if (target instanceof DeviceGroup group) {
      List<Command> commands = new ArrayList<>();

      for (Device leaf : group.getLeaves()) {
        commands.add(on ? new TurnOnCommand(leaf) : new TurnOffCommand(leaf));
      }
      return new MacroCommand((on ? "Turn on" : "Turn off") + group.getName(), commands);
    }

    return on ? new TurnOnCommand(target) : new TurnOffCommand(target);
  }
}
