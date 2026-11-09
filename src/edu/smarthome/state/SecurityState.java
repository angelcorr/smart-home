package edu.smarthome.state;

public interface SecurityState {
    String getName();
    void armHome(SecuritySystem system);

    void armAway(SecuritySystem system);

    void disarm(SecuritySystem system);

    void motionDetected(SecuritySystem system);
}
