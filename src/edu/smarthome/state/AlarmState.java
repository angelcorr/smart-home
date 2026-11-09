package edu.smarthome.state;

public class AlarmState implements SecurityState {

    @Override
    public String getName() {
        return "Alarm";
    }

    @Override
    public void armHome(SecuritySystem system) {
        System.out.println("Alarm is active disarm the system first.");
    }

    @Override
    public void armAway(SecuritySystem system) {
        System.out.println("Alarm is active disarm the system first.");
    }

    @Override
    public void disarm(SecuritySystem system) {
        system.setState(new DisarmedState());
    }

    @Override
    public void motionDetected(SecuritySystem system) {
        System.out.println("Alarm already triggered.");
    }
}
