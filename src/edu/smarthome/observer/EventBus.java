package edu.smarthome.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
  private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();

  // Subscribe
  @SuppressWarnings("unchecked")
  public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
    listeners
        .computeIfAbsent(eventType, k -> new ArrayList<>())
        .add((Consumer<Object>) listener);
  }

  // Notify
  public void publish(Object event) {
    List<Consumer<Object>> subscribed = listeners.get(event.getClass());
    if (subscribed != null) {
      new ArrayList<>(subscribed).forEach(listener -> listener.accept(event));
    }
  }
}
