package edu.smarthome.command;

import edu.smarthome.model.ClimateControl;
import edu.smarthome.strategy.ClimateStrategy;

public class SetClimateStrategyCommand implements Command {
  private final ClimateControl climate;
  private final ClimateStrategy newStrategy;
  private ClimateStrategy previousStrategy;

  public SetClimateStrategyCommand(ClimateControl climate, ClimateStrategy newStrategy) {
    this.climate = climate;
    this.newStrategy = newStrategy;
  }

  @Override
  public void execute() {
    previousStrategy = climate.getStrategy();
    climate.setStrategy(newStrategy);
  }

  @Override
  public void undo() {
    climate.setStrategy(previousStrategy);
  }
}
