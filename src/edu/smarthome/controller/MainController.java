package edu.smarthome.controller;

import edu.smarthome.AppContext;
import edu.smarthome.command.Command;
import edu.smarthome.command.Commands;
import edu.smarthome.command.SetBrightnessCommand;
import edu.smarthome.command.SetClimateStrategyCommand;
import edu.smarthome.factory.DeviceType;
import edu.smarthome.model.*;
import edu.smarthome.observer.DeviceAddedEvent;
import edu.smarthome.observer.DeviceChangedEvent;
import edu.smarthome.strategy.AwayStrategy;
import edu.smarthome.strategy.ClimateStrategy;
import edu.smarthome.strategy.ComfortStrategy;
import edu.smarthome.strategy.EcoStrategy;
import edu.smarthome.observer.SecurityStateChangedEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class MainController {
  private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

  private final AppContext ctx;
  private Device selected;
  private boolean updating;

  @FXML private Button undoButton;
  @FXML private ComboBox<DeviceType> typeCombo;
  @FXML private TextField nameField;
  @FXML private ComboBox<Room> roomCombo;
  @FXML private TreeView<Device> homeTree;
  @FXML private Label nameLabel;
  @FXML private Label stateLabel;
  @FXML private HBox powerBox;
  @FXML private HBox brightnessBox;
  @FXML private Slider brightnessSlider;
  @FXML private HBox climateBox;
  @FXML private ComboBox<ClimateStrategy> strategyCombo;
  @FXML private TextField tempField;
  @FXML private ListView<String> logList;
  @FXML private Label securityLabel;

  public MainController(AppContext ctx) {
    this.ctx = ctx;
  }

  @FXML
  public void initialize() {
    setUpTree();
    setupAddForm();
    setUpStrategyCombo();

    // light command
    brightnessSlider.setOnMouseReleased(e -> {
      if (selected instanceof Light light) {
        run(new SetBrightnessCommand(light, (int) brightnessSlider.getValue()));
      }
    });

    ctx.bus.subscribe(DeviceChangedEvent.class, e -> Platform.runLater(() -> {
      log(e.device().getName() + ": " + e.description());
      homeTree.refresh();
      showDetail(selected);
      updateUndoButton();
    }));

    ctx.bus.subscribe(DeviceAddedEvent.class, e -> Platform.runLater(() -> {
      log(e.device().getName() + " added to " + e.room().getName());
      rebuildTree();
    }));
    ctx.bus.subscribe(SecurityStateChangedEvent.class, e -> Platform.runLater(() -> {
      securityLabel.setText("Security: " + e.stateName());
      log("Security: " + e.stateName());
    }));

    showDetail(null);
    updateUndoButton();

    System.out.println("root = " + homeTree.getRoot());
    System.out.println("children = " + ctx.home.getChildren().size());
  }

  // Composite implementation
  private void setUpTree() {
    homeTree.setCellFactory(tv -> new TreeCell<>() {
      @Override
      protected void updateItem(Device device, boolean empty) {
        super.updateItem(device, empty);
        setText(empty || device == null ? null : device.getName() + " (" + device.getState() + ")");
      }
    });

    homeTree.getSelectionModel().selectedItemProperty().addListener((obs, old, item) -> {
      selected = item == null ? null : item.getValue();
      showDetail(selected);
    });
    rebuildTree();
  }

  private void rebuildTree() {
    Device previous = selected;
    homeTree.setRoot(buildItem(ctx.home));
    homeTree.setShowRoot(true);
    homeTree.getRoot().setExpanded(true);
    if (previous != null) {
      TreeItem<Device> again = find(homeTree.getRoot(), previous);
      if (again != null) {
        homeTree.getSelectionModel().select(again);
      }
    }

  }

  private TreeItem<Device> buildItem(Device device) {
    TreeItem<Device> item = new TreeItem<>(device);
    item.setExpanded(true);
    if (device instanceof DeviceGroup group) {
      for (Device child : group.getChildren()) {
        item.getChildren().add(buildItem(child));
      }
    }

    return item;
  }

  private TreeItem<Device> find(TreeItem<Device> item, Device target) {
    if (item.getValue() == target) {
      return item;
    }

    for (TreeItem<Device> child : item.getChildren()) {
      TreeItem<Device> found = find(child, target);
      if (found != null) {
        return found;
      }
    }

    return null;
  }

  // Add device through Mediator
  private void setupAddForm() {
    typeCombo.getItems().setAll(DeviceType.values());
    typeCombo.getSelectionModel().selectFirst();

    List<Room> rooms = new ArrayList<>();
    collectRooms(ctx.home, rooms);
    roomCombo.getItems().setAll(rooms);
    roomCombo.getSelectionModel().selectFirst();
  }

  private void collectRooms(DeviceGroup group, List<Room> out) {
    for (Device d : group.getChildren()) {
      if (d instanceof Room room) {
        out.add(room);
      } else if (d instanceof DeviceGroup g) {
        collectRooms(g, out);
      }
    }
  }

  @FXML
  private void onAddDevice() {
    DeviceType type = typeCombo.getValue();
    Room room = roomCombo.getValue();
    String name = nameField.getText() == null ? "" : nameField.getText().trim();
    if (type == null || room == null || name.isEmpty()) {
      log("Choose a type, a room and write a name for the device");
      return;
    }

    try {
      ctx.mediator.createDevice(type, name, room);
      nameField.clear();
    } catch (IllegalArgumentException ex) {
      log("Could not load device: " + ex.getMessage());
    }
  }

  private void setUpStrategyCombo() {
    strategyCombo.getItems().setAll(new ComfortStrategy(), new EcoStrategy(), new AwayStrategy());
    strategyCombo.setConverter(new StringConverter<>() {
      @Override
      public String toString(ClimateStrategy s) {
        return s == null ? "" : s.getName();
      }

      @Override
      public ClimateStrategy fromString(String text) {
        return null;
      }
    });

    strategyCombo.setOnAction(e -> {
      if (updating || strategyCombo.getValue() == null) return;

      if (selected instanceof ClimateControl climate) {
        boolean same = climate.getStrategy().getName().equals(strategyCombo.getValue().getName());
        if (!same) {
          run(new SetClimateStrategyCommand(climate, strategyCombo.getValue()));
        }
      }
    });
  }

  private void showDetail(Device device) {
    updating = true;

    try {
      boolean has = device != null;
      nameLabel.setText(has ? device.getName() : "Select an item in the tree");
      stateLabel.setText(has ? device.getState() : "");
      show(powerBox, has);

      boolean isLight = device instanceof Light;
      show(brightnessBox, isLight);
      if (device instanceof Light light) brightnessSlider.setValue(light.getBrightness());

      boolean isClimate = device instanceof ClimateControl;
      show(climateBox, isClimate);
      if (device instanceof ClimateControl climate) {
        String current = climate.getStrategy().getName();
        strategyCombo.getItems().stream().filter(s -> s.getName().equals(current)).findFirst().ifPresent(strategyCombo::setValue);
        tempField.setText(String.valueOf(climate.getCurrentTemperature()));
      }
    } finally {
      updating = false;
    }
  }

  private static void show(Node node, boolean visible) {
    node.setVisible(visible);
    node.setManaged(visible);
  }

  // Commands
  @FXML private void onTurnOn()  {
    if (selected != null) run(Commands.turnOn(selected));
  }

  @FXML private void onTurnOff() {
    if (selected != null) run(Commands.turnOff(selected));
  }

  @FXML private void onAllOff()  {
    run(Commands.turnOff(ctx.home));
  }

  @FXML
  private void onArmHome() {
    ctx.mediator.armHome();
  }

  @FXML
  private void onArmAway() {
    ctx.mediator.armAway();
  }

  @FXML
  private void onDisarm() {
    ctx.mediator.disarm();
  }

  @FXML
  private void onSimulateMotion() {
    for (Device device : ctx.mediator.getDevices()) {
      if (device instanceof MotionSensor sensor) {
        sensor.detectMotion();
        return;
      }
    }
    log("No motion sensor in the home");
  }

  @FXML
  private void onUndo() {
    ctx.history.undo();
    updateUndoButton();
  }

  @FXML
  private void onSetTemperature() {
    if (selected instanceof ClimateControl climate) {
      try {
        climate.setCurrentTemperature(Double.parseDouble(tempField.getText().trim().replace(',', '.')));
      } catch (NumberFormatException ex) {
        log("Invalid temperature");
      }
    }
  }

  private void run(Command command) {
    ctx.history.execute(command);
    updateUndoButton();
  }

  private void updateUndoButton() {
    undoButton.setDisable(!ctx.history.canUndo());
  }

  private void log(String text) {
    logList.getItems().add(0, LocalTime.now().format(TIME) + " " + text);
  }

}
