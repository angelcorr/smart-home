package edu.smarthome.application;

import edu.smarthome.command.*;
import edu.smarthome.factory.DeviceCreator;
import edu.smarthome.factory.DeviceCreatorRegistry;
import edu.smarthome.factory.DeviceType;
import edu.smarthome.model.Device;
import edu.smarthome.model.Room;
import edu.smarthome.observer.EventBus;
import edu.smarthome.model.Light;
import edu.smarthome.model.Television;
import edu.smarthome.observer.DeviceChangedEvent;


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
    private CommandHistory history = new CommandHistory();

    public SmartHomeMediator(EventBus bus, DeviceCreatorRegistry registry, CommandHistory history) {
        this.bus = bus;
        this.registry = registry;
        this.history = history;
        bus.subscribe(DeviceChangedEvent.class, this::onDeviceChanged);
    }

    public Device createDevice(DeviceType type, String name, Room room) {
        DeviceCreator creator = registry.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unsupported device type: " + type);
        }
        Device device = creator.create(name, room, bus);
        devices.put(device.getId(), device);
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
        history.execute(command);
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
            if (device instanceof Light light) {
                execute(new SetBrightnessCommand(light, brightness));
            }
        }
    }

}
