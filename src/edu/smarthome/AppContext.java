package edu.smarthome;

import edu.smarthome.application.SmartHomeMediator;
import edu.smarthome.command.CommandHistory;
import edu.smarthome.factory.DeviceCreatorRegistry;
import edu.smarthome.model.DeviceGroup;
import edu.smarthome.observer.EventBus;

public class AppContext {
  public final EventBus bus = new EventBus();
  public final DeviceCreatorRegistry registry = new DeviceCreatorRegistry();
  public final CommandHistory history = new CommandHistory();
  public final DeviceGroup home = new DeviceGroup("hm-1", "Gianna's home"); // Composite root
  public final SmartHomeMediator mediator = new SmartHomeMediator(bus, registry, history);
}
