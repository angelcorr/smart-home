package edu.smarthome.command;

public interface Command {
    void execute();
    void undo();
}