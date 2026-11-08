package edu.smarthome.command;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {

    private final List<Command> history = new ArrayList<>();

    // Saves a command after it was executed.
    public void push(Command command) {
        history.add(command);
    }

    // Undoes the most recent command and removes it from the history.
    public void undo() {
        if (!history.isEmpty()) {
            history.remove(history.size() - 1).undo();
        }
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}