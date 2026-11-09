package edu.smarthome.application;

import edu.smarthome.command.Command;
import edu.smarthome.command.CommandHistory;
import edu.smarthome.factory.DeviceCreator;
import edu.smarthome.factory.DeviceCreatorRegistry;
import edu.smarthome.factory.DeviceType;
import edu.smarthome.model.Device;
import edu.smarthome.model.Room;
import edu.smarthome.observer.EventBus;
import edu.smarthome.model.Light;
import edu.smarthome.model.Television;
import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.command.TurnOnCommand;
import edu.smarthome.command.TurnOffCommand;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// los dispositivos actuan de forma individual nunca se comunican entre si
public class SmartHomeMediator {

    private final EventBus bus;
    private final DeviceCreatorRegistry registry;
    private final Map<String, Device> devices = new HashMap<>();
    private final Map<Device, Room> deviceRooms = new HashMap<>();
    private final CommandHistory history = new CommandHistory();

    public SmartHomeMediator(EventBus bus, DeviceCreatorRegistry registry) {
        this.bus = bus;
        this.registry = registry;
        bus.subscribe(DeviceChangedEvent.class, this::onDeviceChanged);
    }

    public Device createDevice(DeviceType type, String id, String name, Room room) {
        if (devices.containsKey(id)) {
            throw new IllegalArgumentException("A device with id " + id + " already exists");
        }
        DeviceCreator creator = registry.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unsupported device type: " + type);
        }
        Device device = creator.create(name, room, bus);
        devices.put(id, device);
        deviceRooms.put(device, room);
        return device;
    }

    public Device findDevice(String id) {
        Device device = devices.get(id);
        if (device == null) {
            throw new IllegalArgumentException("No device with id " + id);
        }
        return device;
    }

    public void turnOn(String id) {
        execute(new TurnOnCommand(findDevice(id)));
    }

    public void turnOff(String id) {
        execute(new TurnOffCommand(findDevice(id)));
    }

    public Collection<Device> getDevices() {
        return Collections.unmodifiableCollection(devices.values());
    }

    public void execute(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        history.undo();
    }

    private void onDeviceChanged(DeviceChangedEvent event) {
        Device changed = event.device();
        if (changed instanceof Television && changed.isOn() && "turned on".equals(event.description())) {
            dimLightsInRoomOf(changed, 30);
        }
    }

    private void dimLightsInRoomOf(Device source, int brightness) {
        Room room = deviceRooms.get(source);
        if (room == null) {
            return;
        }
        for (Device device : room.getDevices()) {
            if (device instanceof Light) {
                ((Light) device).setBrightness(brightness);
            }
        }
    }

}
