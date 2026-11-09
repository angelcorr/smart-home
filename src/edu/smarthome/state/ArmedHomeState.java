package edu.smarthome.state;

public class ArmedHomeState implements SecurityState {

    @Override
    public String getName() {
        return "ArmedHome";
    }

    @Override
    public void armHome(SecuritySystem system) {
        System.out.println("Already armed at home.");
    }

    @Override
    public void armAway(SecuritySystem system) {
        system.setState(new ArmedAwayState());
    }

    @Override
    public void disarm(SecuritySystem system) {
        system.setState(new DisarmedState());
    }

    @Override
    public void motionDetected(SecuritySystem system) {
        System.out.println(" someone is at home.");
    }
}
