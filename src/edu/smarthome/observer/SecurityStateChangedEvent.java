package edu.smarthome.observer;

public final class SecurityStateChangedEvent {

    private final String stateName;

    public SecurityStateChangedEvent(String stateName) {
        this.stateName = stateName;
    }

    public String stateName() {
        return stateName;
    }
}
