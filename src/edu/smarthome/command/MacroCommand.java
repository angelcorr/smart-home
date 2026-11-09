package edu.smarthome.command;

import java.util.List;

public class MacroCommand implements Command {
  private final String name;
  private final List<Command> commands;

  public MacroCommand(String name, List<Command> commands) {
    this.name = name;
    this.commands = List.copyOf(commands);
  }

  @Override
  public void execute() {
    commands.forEach(Command::execute);
  }

  @Override
  public void undo() {
    for (int i = commands.size() -1; i >= 0; i--) {
      commands.get(i).undo();
    }
  }

  public String getName() {
    return name;
  }
}
