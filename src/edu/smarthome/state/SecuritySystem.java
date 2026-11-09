package edu.smarthome.state;

import edu.smarthome.observer.EventBus;
import edu.smarthome.observer.SecurityStateChangedEvent;

public class SecuritySystem {

    private final EventBus bus;
    private SecurityState state;

    public SecuritySystem(EventBus bus) {
        this.bus = bus;
        this.state = new DisarmedState();
    }

    public void setState(SecurityState newState) {
        this.state = newState;
        bus.publish(new SecurityStateChangedEvent(newState.getName()));
    }

    public String getStateName() {
        return state.getName();
    }

    public void armHome() {
        state.armHome(this);
    }

    public void armAway() {
        state.armAway(this);
    }

    public void disarm() {
        state.disarm(this);
    }

    public void motionDetected() {
        state.motionDetected(this);
    }
}
