package edu.smarthome.command;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {

    private final List<Command> history = new ArrayList<>();

    public void execute(Command command) {
        command.execute();
        history.add(command);
    }

    // Saves a command after it was executed.
    public void push(Command command) {
        history.add(command);
    }

    // Undoes the most recent command and removes it from the history.
    public void undo() {
        if (!history.isEmpty()) {
            history.removeLast().undo(); // Same logic as before but written clean
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}
