package edu.smarthome.state;

public class DisarmedState implements SecurityState {

    @Override
    public String getName() {
        return "Disarmed";
    }

    @Override
    public void armHome(SecuritySystem system) {
        system.setState(new ArmedHomeState());
    }

    @Override
    public void armAway(SecuritySystem system) {
        system.setState(new ArmedAwayState());
    }

    @Override
    public void disarm(SecuritySystem system) {
        System.out.println("Already disarmed.");
    }

    @Override
    public void motionDetected(SecuritySystem system) {
        System.out.println(" system is disarmed.");
    }
}
