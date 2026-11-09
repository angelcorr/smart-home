package edu.smarthome.state;

public class ArmedAwayState implements SecurityState {

    @Override
    public String getName() {
        return "ArmedAway";
    }

    @Override
    public void armHome(SecuritySystem system) {
        system.setState(new ArmedHomeState());
    }

    @Override
    public void armAway(SecuritySystem system) {
        System.out.println("Already armed away.");
    }

    @Override
    public void disarm(SecuritySystem system) {
        system.setState(new DisarmedState());
    }

    @Override
    public void motionDetected(SecuritySystem system) {
        system.setState(new AlarmState());
    }
}
