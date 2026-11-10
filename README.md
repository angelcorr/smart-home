# Smart Home Management System

Desktop application for managing and automating a smart home, built with **Java 21, JavaFX and FXML**. It is the Parcial 3 project (behavioral patterns) for Programación 2.

**Team:** `Angeles Rondon`, `Daniel Monsalve`.

---

## 1. Description and features

The app models a house as a hierarchy (home → floor → room → device) and lets the user control it from one window. Devices never talk to each other directly: they publish events, and a mediator applies the automation rules.

**Features**

- Home tree showing every floor, room and device with its live state.
- Device types: light (brightness), television, blind, thermostat, legacy vendor thermostat, door lock and motion sensor.
- Add devices from a form (type, name, room).
- Turn on / turn off any selection: a single device, a room or a whole floor.
- Undo for every action, including group actions ("All off", away mode).
- Thermostat modes (Comfort, Eco, Away) that can be switched at runtime, plus a simulated room temperature.
- Security system with four states: Disarmed, ArmedHome, ArmedAway and Alarm, with a motion sensor simulation.
- Automation rules (see the Mediator section).
- Event log that updates by itself.

---

## 2. How to run

The project does **not** use Maven or Gradle. It is a plain IntelliJ project, so you only need a JDK 21 that includes JavaFX.

### Requirements

- **JDK 21 with JavaFX.** The easiest option is **BellSoft Liberica Full JDK 21**, which bundles JavaFX. Download it at bell-sw.com, or from IntelliJ (see below).
- IntelliJ IDEA (Community is enough).
- Git.

### Option A: IntelliJ with Liberica Full JDK 21 (recommended)

1. Clone the repository:
   ```bash
   git clone <repository URL>
   ```
2. In IntelliJ: **File → Open**, and select the cloned folder.
3. **File → Project Structure → Project**:
    - **SDK:** choose a JDK 21 (**Add SDK → Download JDK**, version 21, vendor *BellSoft Liberica Full JDK*).
    - **Language level:** 21.
4. **File → Project Structure → Modules → Sources**:
    - Mark the `src` folder as **Sources**.
    - Mark the `resources` folder as **Resources**.

   (Or right-click each folder → **Mark Directory as**.)
5. Open `src/edu/smarthome/Main.java` and click the green **Run** button.

No VM options are needed with this JDK.

### Option B: JDK 21 plus a separate JavaFX SDK

1. Install any JDK 21 and download the **JavaFX SDK 21** for your system from gluonhq.com/products/javafx.
2. Follow steps 1 to 4 of Option A, using your JDK 21.
3. **File → Project Structure → Libraries → + → Java**, select the SDK's `lib` folder and add it to the module.
4. **Run → Edit Configurations**, select the `Main` configuration, then **Modify options → Add VM options**, and enter:
   ```
   --module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml
   ```
   On Windows, use the same flags with a Windows path, for example `C:\javafx-sdk-21\lib`.

### Option C: Command line (no IDE)

From the repository root (macOS or Linux; set `FX` to the JavaFX `lib` folder, which is not needed with Liberica Full):

```bash
FX=/path/to/javafx-sdk-21/lib
mkdir -p out
javac --module-path $FX --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")
cp -R resources/* out/
java --module-path $FX --add-modules javafx.controls,javafx.fxml -cp out edu.smarthome.Main
```

The `cp` step matters: it puts `view/main.fxml` and `css/app.css` on the classpath, where the app looks for them.

### Common problems

| Message | Cause |
|---|---|
| `Location is not set` / NullPointerException on `getResource` | `resources` is not marked as Resources Root |
| `JavaFX runtime components are missing` | The JDK has no JavaFX, or the VM options (Option B) are missing |
| `release version 21 not supported` | The project SDK is older than 21 |

---

## 3. Quick tour (what to try)

1. Select a light in the tree, press **Turn on**, then **Undo**.
2. Select **Upstairs** and press **Turn on**: every device below it turns on with one action. **Undo** restores each device to its previous state.
3. Select a thermostat, change the mode to **Away** and set a room temperature.
4. Add a TV and a light in the same room, then turn the TV on: the lights dim to 30%.
5. In the Security panel, pick the motion sensor and press **Trigger sensor** in each state (Disarmed, ArmedHome, ArmedAway).

---

## 4. Project structure

```
smart-home/
├── src/edu/smarthome/
│   ├── Main.java                 JavaFX entry point, creates the starting house
│   ├── AppContext.java           shared objects (bus, registry, history, home, mediator)
│   ├── adapter/                  LegacyThermostatAdapter and the vendor class in adapter/legacy
│   ├── application/              SmartHomeMediator
│   ├── command/                  Command, CommandHistory, concrete commands, MacroCommand, Commands
│   ├── controller/               JavaFX controller (MainController)
│   ├── factory/                  DeviceCreator hierarchy, DeviceType, DeviceCreatorRegistry
│   ├── model/                    Device, DeviceBase, DeviceGroup, Room, concrete devices
│   ├── observer/                 EventBus and event classes
│   ├── state/                    SecuritySystem and the four security states
│   └── strategy/                 ClimateStrategy and its implementations
├── resources/
│   ├── view/main.fxml            the user interface
│   └── css/app.css               styles
└── README.md
```

---

## 5. Design patterns

### Mandatory behavioral patterns

#### Observer
- **Location:** `observer/` (`EventBus`, `DeviceChangedEvent`, `DeviceAddedEvent`, `MotionDetectedEvent`, `SecurityStateChangedEvent`, ...).
- **Problem:** many parts of the app need to know when a device changes, but devices should not know the UI.
- **Use:** devices publish events through the `EventBus`. The event log, the home tree, the detail panel, the security label and the mediator subscribe to the events they care about. Nobody refreshes the screen manually.

#### Strategy
- **Location:** `strategy/` (`ClimateStrategy`, `ComfortStrategy`, `EcoStrategy`, `AwayStrategy`); context in `model/Thermostat`.
- **Problem:** the thermostat must choose its target temperature in different ways depending on the mode, without `if/else` chains.
- **Use:** the thermostat delegates to its current strategy (`targetTemperature(time)`). The mode combo in the detail panel swaps the strategy at runtime. Eco depends on the time of day, Comfort is constant and Away is minimal.

#### Command
- **Location:** `command/` (`Command`, `CommandHistory`, `TurnOnCommand`, `TurnOffCommand`, `SetBrightnessCommand`, `SetClimateStrategyCommand`, `MacroCommand`, `Commands`).
- **Problem:** user actions must be undoable, and one action sometimes involves many devices.
- **Use:** every UI action runs through `CommandHistory.execute(...)`. Each command stores the previous state to undo itself. Group actions (turn off a room, "All off", away mode) are `MacroCommand`s made of one command per device, so each device is restored to its own previous state.

#### State
- **Location:** `state/` (`SecurityState`, `DisarmedState`, `ArmedHomeState`, `ArmedAwayState`, `AlarmState`; context `SecuritySystem`).
- **Problem:** the security system reacts differently to the same event depending on its state.
- **Use:** `SecuritySystem` only delegates to the current state. Each state decides what arming, disarming and motion do, and performs its own transitions (for example, motion in ArmedAway moves to Alarm, while in Disarmed it is ignored). Every change publishes a `SecurityStateChangedEvent`.

#### Mediator
- **Location:** `application/SmartHomeMediator`.
- **Problem:** devices, the security system and the commands must cooperate without referencing each other.
- **Use:** devices only publish events. The mediator subscribes and holds the rules:

| Trigger | Reaction |
|---|---|
| Television turned on | Dims the lights in that room |
| Motion detected | Security system reacts; if the system is ArmedHome, the room's lights turn on |
| Security: ArmedAway | Locks doors, turns lights off, sets thermostats to the Away strategy |
| Security: Alarm | Turns all lights on |
| Security: Disarmed | Unlocks doors |

The mediator also creates devices (through the factories) so it knows every device and its room.

### Additional patterns

Selected: Factory Method (2) + Adapter (2) + Composite (3) = **7** points of complexity, with one creational and two structural patterns.

#### Factory Method (creational)
- **Location:** `factory/` (`DeviceCreator` as the abstract creator, `LightCreator`, `ThermostatCreator`, `LegacyThermostatCreator`, ... as concrete creators; `DeviceType` and `DeviceCreatorRegistry` for the UI).
- **Why it fits:** each device type needs its own setup (for example, the thermostat gets a default strategy). The "Add device" form works with a `DeviceType` and never names concrete classes. A new device type needs one new creator and one registry line.
- **How it works:** `DeviceCreator.create(...)` generates the id, calls the abstract factory method `createDevice(...)`, adds the device to its room and publishes `DeviceAddedEvent`.

#### Adapter (structural)
- **Location:** `adapter/LegacyThermostatAdapter` (adapter), `adapter/legacy/LegacyThermostat` (adaptee), `model/ClimateControl` (target interface).
- **Why it fits:** the vendor thermostat uses Fahrenheit, whole degrees and numeric status codes, and cannot be modified.
- **How it works:** the adapter implements `ClimateControl`, converts units, translates status codes into `HvacMode`, and feeds the legacy device the target chosen by the current strategy. The UI treats it exactly like the modern thermostat.

#### Composite (structural)
- **Location:** `model/Device` (component), `model/DeviceGroup` (composite), `model/Room` (extends `DeviceGroup`); leaves are `Light`, `Television`, `Blind`, `Thermostat`, `DoorLock`, `MotionSensor` and `LegacyThermostatAdapter`.
- **Why it fits:** the user must be able to act on one device, a room or a whole floor in the same way.
- **How it works:** a group implements `Device` and forwards `turnOn()` / `turnOff()` to its children recursively. The home tree is built recursively from the same structure.

---

## 6. Technical notes and limitations

- Java 21 with JavaFX and FXML. No Maven or Gradle.
- Security state changes are events, not edits, so they are not part of Undo. The automatic reactions (away mode, alarm lights) are commands and can be undone.
- "All off" also unlocks door locks.
- The legacy thermostat stores whole degrees Fahrenheit, so temperatures read back from it can differ slightly from the value entered.
- Room temperature is simulated by typing a value; there are no real sensors.

Spanish version

# Sistema de Gestión y Automatización para Hogares Inteligentes

Aplicación de escritorio para gestionar y automatizar una vivienda inteligente, desarrollada con **Java 21, JavaFX y FXML**. Es el proyecto del Parcial 3 (patrones de comportamiento) de Programación 2.

---

## 1. Descripción y funcionalidades

La aplicación modela una casa como una jerarquía (casa → piso → habitación → dispositivo) y permite controlarla desde una sola ventana. Los dispositivos nunca se comunican directamente entre sí: publican eventos, y un mediador aplica las reglas de automatización.

**Funcionalidades**

- Árbol de la casa que muestra pisos, habitaciones y dispositivos con su estado en tiempo real.
- Tipos de dispositivo: luz (con brillo), televisor, persiana, termostato, termostato de un proveedor antiguo (legacy), cerradura de puerta y sensor de movimiento.
- Agregar dispositivos desde un formulario (tipo, nombre, habitación).
- Encender / apagar cualquier selección: un dispositivo, una habitación o un piso completo.
- Deshacer (Undo) para todas las acciones, incluidas las de grupo ("All off", modo ausente).
- Modos del termostato (Comfort, Eco, Away) intercambiables en tiempo de ejecución, y temperatura ambiente simulada.
- Sistema de seguridad con cuatro estados: Disarmed, ArmedHome, ArmedAway y Alarm, con simulación de un sensor de movimiento.
- Reglas de automatización (ver la sección del Mediator).
- Registro de eventos que se actualiza solo.

---

## 2. Cómo ejecutar

El proyecto **no** usa Maven ni Gradle. Es un proyecto simple de IntelliJ, así que solo se necesita un JDK 21 que incluya JavaFX.

### Requisitos

- **JDK 21 con JavaFX.** La opción más fácil es **BellSoft Liberica Full JDK 21**, que ya incluye JavaFX. Se descarga en bell-sw.com o desde IntelliJ (ver abajo).
- IntelliJ IDEA (la versión Community es suficiente).
- Git.

### Opción A: IntelliJ con Liberica Full JDK 21 (recomendada)

1. Clonar el repositorio:
   ```bash
   git clone <URL del repositorio>
   ```
2. En IntelliJ: **File → Open** y seleccionar la carpeta clonada.
3. **File → Project Structure → Project**:
    - **SDK:** elegir un JDK 21 (**Add SDK → Download JDK**, versión 21, proveedor *BellSoft Liberica Full JDK*).
    - **Language level:** 21.
4. **File → Project Structure → Modules → Sources**:
    - Marcar la carpeta `src` como **Sources**.
    - Marcar la carpeta `resources` como **Resources**.

   (O clic derecho sobre cada carpeta → **Mark Directory as**.)
5. Abrir `src/edu/smarthome/Main.java` y presionar el botón verde **Run**.

Con este JDK no se necesitan opciones de VM.

### Opción B: JDK 21 más un SDK de JavaFX por separado

1. Instalar cualquier JDK 21 y descargar el **JavaFX SDK 21** para su sistema en gluonhq.com/products/javafx.
2. Seguir los pasos 1 a 4 de la Opción A usando su JDK 21.
3. **File → Project Structure → Libraries → + → Java**, seleccionar la carpeta `lib` del SDK y agregarla al módulo.
4. **Run → Edit Configurations**, seleccionar la configuración de `Main`, luego **Modify options → Add VM options** e ingresar:
   ```
   --module-path /ruta/al/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml
   ```
   En Windows se usan las mismas opciones con una ruta de Windows, por ejemplo `C:\javafx-sdk-21\lib`.

### Opción C: Línea de comandos (sin IDE)

Desde la raíz del repositorio (macOS o Linux; `FX` es la carpeta `lib` de JavaFX, que no hace falta con Liberica Full):

```bash
FX=/ruta/al/javafx-sdk-21/lib
mkdir -p out
javac --module-path $FX --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")
cp -R resources/* out/
java --module-path $FX --add-modules javafx.controls,javafx.fxml -cp out edu.smarthome.Main
```

El paso `cp` es importante: coloca `view/main.fxml` y `css/app.css` en el classpath, donde la aplicación los busca.

### Problemas comunes

| Mensaje | Causa |
|---|---|
| `Location is not set` / NullPointerException en `getResource` | `resources` no está marcada como Resources Root |
| `JavaFX runtime components are missing` | El JDK no incluye JavaFX o faltan las opciones de VM (Opción B) |
| `release version 21 not supported` | El SDK del proyecto es anterior a la versión 21 |

---

## 3. Recorrido rápido (qué probar)

1. Seleccionar una luz en el árbol, presionar **Turn on** y luego **Undo**.
2. Seleccionar **Upstairs** y presionar **Turn on**: todos los dispositivos de ese piso se encienden con una sola acción. **Undo** devuelve cada dispositivo a su estado anterior.
3. Seleccionar un termostato, cambiar el modo a **Away** y establecer una temperatura ambiente.
4. Agregar un televisor y una luz en la misma habitación y encender el televisor: las luces bajan al 30 %.
5. En el panel Security, elegir el sensor de movimiento y presionar **Trigger sensor** en cada estado (Disarmed, ArmedHome, ArmedAway).

---

## 4. Estructura del proyecto

```
smart-home/
├── src/edu/smarthome/
│   ├── Main.java                 punto de entrada JavaFX, crea la casa inicial
│   ├── AppContext.java           objetos compartidos (bus, registry, history, home, mediator)
│   ├── adapter/                  LegacyThermostatAdapter y la clase del proveedor en adapter/legacy
│   ├── application/              SmartHomeMediator
│   ├── command/                  Command, CommandHistory, comandos concretos, MacroCommand, Commands
│   ├── controller/               controlador JavaFX (MainController)
│   ├── factory/                  jerarquía DeviceCreator, DeviceType, DeviceCreatorRegistry
│   ├── model/                    Device, DeviceBase, DeviceGroup, Room, dispositivos concretos
│   ├── observer/                 EventBus y clases de eventos
│   ├── state/                    SecuritySystem y los cuatro estados de seguridad
│   └── strategy/                 ClimateStrategy y sus implementaciones
├── resources/
│   ├── view/main.fxml            la interfaz de usuario
│   └── css/app.css               estilos
└── README.md
```

---

## 5. Patrones de diseño

### Patrones de comportamiento obligatorios

#### Observer
- **Ubicación:** `observer/` (`EventBus`, `DeviceChangedEvent`, `DeviceAddedEvent`, `MotionDetectedEvent`, `SecurityStateChangedEvent`, ...).
- **Problema:** muchas partes de la aplicación necesitan saber cuándo cambia un dispositivo, pero los dispositivos no deben conocer la interfaz.
- **Uso:** los dispositivos publican eventos a través del `EventBus`. El registro de eventos, el árbol de la casa, el panel de detalle, la etiqueta de seguridad y el mediador se suscriben a los eventos que les interesan. Nadie refresca la pantalla manualmente.

#### Strategy
- **Ubicación:** `strategy/` (`ClimateStrategy`, `ComfortStrategy`, `EcoStrategy`, `AwayStrategy`); contexto en `model/Thermostat`.
- **Problema:** el termostato debe elegir su temperatura objetivo de formas distintas según el modo, sin cadenas de `if/else`.
- **Uso:** el termostato delega en su estrategia actual (`targetTemperature(hora)`). El combo de modo del panel de detalle cambia la estrategia en tiempo de ejecución. Eco depende de la hora del día, Comfort es constante y Away es mínima.

#### Command
- **Ubicación:** `command/` (`Command`, `CommandHistory`, `TurnOnCommand`, `TurnOffCommand`, `SetBrightnessCommand`, `SetClimateStrategyCommand`, `MacroCommand`, `Commands`).
- **Problema:** las acciones del usuario deben poder deshacerse, y una acción a veces involucra muchos dispositivos.
- **Uso:** toda acción de la interfaz pasa por `CommandHistory.execute(...)`. Cada comando guarda el estado previo para poder deshacerse. Las acciones de grupo (apagar una habitación, "All off", modo ausente) son `MacroCommand`s formados por un comando por dispositivo, de modo que cada dispositivo vuelve a su propio estado anterior.

#### State
- **Ubicación:** `state/` (`SecurityState`, `DisarmedState`, `ArmedHomeState`, `ArmedAwayState`, `AlarmState`; contexto `SecuritySystem`).
- **Problema:** el sistema de seguridad reacciona de forma distinta al mismo evento según su estado.
- **Uso:** `SecuritySystem` solo delega en el estado actual. Cada estado decide qué hacen armar, desarmar y el movimiento, y realiza sus propias transiciones (por ejemplo, el movimiento en ArmedAway pasa a Alarm, mientras que en Disarmed se ignora). Cada cambio publica un `SecurityStateChangedEvent`.

#### Mediator
- **Ubicación:** `application/SmartHomeMediator`.
- **Problema:** los dispositivos, el sistema de seguridad y los comandos deben cooperar sin referenciarse entre sí.
- **Uso:** los dispositivos solo publican eventos. El mediador se suscribe y concentra las reglas:

| Disparador | Reacción |
|---|---|
| Se enciende el televisor | Baja las luces de esa habitación |
| Se detecta movimiento | El sistema de seguridad reacciona; si está en ArmedHome, se encienden las luces de la habitación |
| Seguridad: ArmedAway | Bloquea las puertas, apaga las luces y pone los termostatos en la estrategia Away |
| Seguridad: Alarm | Enciende todas las luces |
| Seguridad: Disarmed | Desbloquea las puertas |

El mediador también crea los dispositivos (mediante las fábricas), por lo que conoce cada dispositivo y su habitación.

### Patrones adicionales

Seleccionados: Factory Method (2) + Adapter (2) + Composite (3) = **7** puntos de complejidad, con un patrón creacional y dos estructurales.

#### Factory Method (creacional)
- **Ubicación:** `factory/` (`DeviceCreator` como creador abstracto; `LightCreator`, `ThermostatCreator`, `LegacyThermostatCreator`, ... como creadores concretos; `DeviceType` y `DeviceCreatorRegistry` para la interfaz).
- **Por qué es apropiado:** cada tipo de dispositivo requiere su propia configuración (por ejemplo, el termostato recibe una estrategia por defecto). El formulario "Add device" trabaja con un `DeviceType` y nunca nombra clases concretas. Un nuevo tipo de dispositivo requiere un creador nuevo y una línea en el registro.
- **Cómo funciona:** `DeviceCreator.create(...)` genera el id, llama al método fábrica abstracto `createDevice(...)`, agrega el dispositivo a su habitación y publica `DeviceAddedEvent`.

#### Adapter (estructural)
- **Ubicación:** `adapter/LegacyThermostatAdapter` (adaptador), `adapter/legacy/LegacyThermostat` (adaptado), `model/ClimateControl` (interfaz objetivo).
- **Por qué es apropiado:** el termostato del proveedor usa grados Fahrenheit enteros y códigos de estado numéricos, y no se puede modificar.
- **Cómo funciona:** el adaptador implementa `ClimateControl`, convierte unidades, traduce los códigos de estado a `HvacMode` y le entrega al dispositivo legacy la temperatura objetivo de la estrategia actual. La interfaz lo trata exactamente igual que al termostato moderno.

#### Composite (estructural)
- **Ubicación:** `model/Device` (componente), `model/DeviceGroup` (compuesto), `model/Room` (extiende `DeviceGroup`); las hojas son `Light`, `Television`, `Blind`, `Thermostat`, `DoorLock`, `MotionSensor` y `LegacyThermostatAdapter`.
- **Por qué es apropiado:** el usuario debe poder actuar de la misma forma sobre un dispositivo, una habitación o un piso completo.
- **Cómo funciona:** un grupo implementa `Device` y reenvía `turnOn()` / `turnOff()` a sus hijos de forma recursiva. El árbol de la casa se construye recursivamente a partir de la misma estructura.

---

## 6. Notas técnicas y limitaciones

- Java 21 con JavaFX y FXML. Sin Maven ni Gradle.
- Los cambios de estado de seguridad son eventos, no ediciones, por lo que no forman parte de Undo. Las reacciones automáticas (modo ausente, luces de alarma) sí son comandos y se pueden deshacer.
- "All off" también desbloquea las cerraduras de las puertas.
- El termostato legacy guarda grados Fahrenheit enteros, por lo que las temperaturas que devuelve pueden diferir ligeramente del valor ingresado.
- La temperatura ambiente se simula escribiendo un valor; no hay sensores reales.
