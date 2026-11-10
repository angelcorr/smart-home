package edu.smarthome;

import edu.smarthome.controller.MainController;
import edu.smarthome.factory.DeviceType;
import edu.smarthome.model.DeviceGroup;
import edu.smarthome.model.Room;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    AppContext ctx = new AppContext();
    seedHome(ctx);

    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/main.fxml"));
    loader.setControllerFactory(type -> new MainController(ctx));

    stage.setScene(new Scene(loader.load()));
    stage.setTitle("Smart Home");
    stage.setMinWidth(1100);
    stage.setMinHeight(700);
    stage.show();
  }

  private static void seedHome(AppContext ctx) {
    Room living = new Room("Living room");
    Room kitchen = new Room("kitchen room");
    Room bedroom = new Room("bedroom room");
    Room office = new Room("office room");
    Room bathroom = new Room("Bathroom");

    DeviceGroup upstairs = new DeviceGroup("floor-2", "Upstairs");
    upstairs.add(bedroom);
    upstairs.add(office);
    upstairs.add(bathroom);

    ctx.home.add(living);
    ctx.home.add(kitchen);
    ctx.home.add(upstairs);

    ctx.mediator.createDevice(DeviceType.LIGHT, "Ceiling light living room", living);
    ctx.mediator.createDevice(DeviceType.LIGHT, "Ceiling light bathroom", living);
    ctx.mediator.createDevice(DeviceType.TELEVISION, "TV", living);
    ctx.mediator.createDevice(DeviceType.THERMOSTAT, "Main thermostat", living);
    ctx.mediator.createDevice(DeviceType.LIGHT, "Kitchen light", kitchen);
    ctx.mediator.createDevice(DeviceType.LIGHT, "Bedside light", bedroom);
    ctx.mediator.createDevice(DeviceType.LEGACY_THERMOSTAT, "Old vendor thermostat in the bedroom", bedroom);
    ctx.mediator.createDevice(DeviceType.LIGHT, "Desk light", office);
    ctx.mediator.createDevice(DeviceType.LIGHT, "Light bathroom second floor", office);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
