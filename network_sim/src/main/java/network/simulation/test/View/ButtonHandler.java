package network.simulation.test.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import network.simulation.test.Controller.IControllerView;
import network.simulation.test.Model.IModelView;

public class ButtonHandler {
    IControllerView controller;
    IModelView model;
    MainView view;
    public ButtonHandler(IModelView model, MainView view, IControllerView controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    //Handlers for devices

    /**
     * Handles adding a new device to the project.
     * Prompts the user to choose between a standard or custom device.
     * If standard, calls the controller to add a standard device.
     * If custom, prompts for device details and calls the controller to add a custom device.
     */
    protected void handleAddDevice() {
        ChoiceDialog<String> choice = new ChoiceDialog<>("Standard", "Standard", "DNS", "Web", "Printer", "Firewall", "Custom");
        choice.setTitle("Add Device");
        choice.setHeaderText("Choose device type:");
        choice.setContentText("Type:");

        Optional<String> result = choice.showAndWait();
        result.ifPresent(type -> {
            if (!type.equals("Custom")) {
                controller.onClick("createDevice", type.toLowerCase());
            } else {
                // Prompt for custom device info
                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setTitle("Custom Device");
                dialog.setHeaderText("Enter device details");

                ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField nameField = new TextField();
                TextField imageField = new TextField();

                grid.add(new Label("Device name:"), 0, 0);
                grid.add(nameField, 1, 0);
                grid.add(new Label("Base image (e.g. ubuntu:22.04):"), 0, 1);
                grid.add(imageField, 1, 1);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == createButtonType) {
                        return new Pair<>(nameField.getText(), imageField.getText());
                    }
                    return null;
                });

                Optional<Pair<String, String>> deviceInfo = dialog.showAndWait();
                deviceInfo.ifPresent(info -> {
                    controller.onClick("createCustomDevice", info.getKey(), info.getValue());
                });
            }
        });
        this.view.updateDisplay();
    }

    /**
     * Handles editing a device.
     * Opens a dialog to edit the device details such as name, IP, OS, and services.
     * On saving, it calls the controller to update the device and refreshes the display.
     *
     * @param deviceItem The TreeItem representing the device to be edited.
     */
    protected void handleEditDevice(TreeItem<String> deviceItem) {
        Dialog<DeviceEditResult> dialog = new Dialog<>();
        dialog.setTitle("Edit Device");
        dialog.setHeaderText("Edit device details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField nameField = new TextField(deviceItem.getValue());
        TextField osField = new TextField();
        TextField servicesField = new TextField();
        TextField entryPointField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Device name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Operating System:"), 0, 2);
        grid.add(osField, 1, 2);
        grid.add(new Label("Services (comma-separated):"), 0, 3);
        grid.add(servicesField, 1, 3);
        grid.add(new Label("Is this the entry-point? (true or false)"), 0, 4);
        grid.add(entryPointField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nameField::requestFocus);
        
        // Convert result when Save is pressed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new DeviceEditResult(
                    nameField.getText(),
                    osField.getText(),
                    servicesField.getText(),
                    entryPointField.getText()
                );
            }
            return null;
        });

        Optional<DeviceEditResult> result = dialog.showAndWait();

        result.ifPresent(edited -> {
            controller.onClick("editDevice",
                deviceItem.getValue(), // old name
                edited.getName(),      // new name
                edited.getOs(),
                edited.getServices(),
                edited.getIsEntryPoint()
            );
        });
        this.view.updateDisplay();
    }

    /**
    * Handles adding a device to a network.
    * Prompts the user to choose a network from the available networks.
    * On selection, it calls the controller to assign the device to the selected network and refreshes the display.
    *
    * @param deviceItem The TreeItem representing the device to be added to a network.
    */
    protected void handleAddToNetwork(TreeItem<String> deviceItem) {
        List<String> networks = model.getNetworkNames();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(networks.get(0), networks);
        dialog.setTitle("Assign to Network");
        dialog.setHeaderText("Choose a network:");

        dialog.showAndWait().ifPresent(network -> {
            controller.onClick("assignDeviceToNetwork", deviceItem.getValue(), network);
        });
        this.view.updateDisplay();
    }

    /**
    * Handles deleting a device from the project.
    * Calls the controller to delete the device and refreshes the display.
    *
    * @param deviceItem The TreeItem representing the device to be deleted.
    */
    protected void handleDeleteDevice(TreeItem<String> deviceItem) {
        String deviceName = deviceItem.getValue();
        String deviceHome = model.findDevice(deviceName);
        controller.onClick("deleteDevice", deviceName, deviceHome);
        this.view.updateDisplay();
    }
    
    //Handlers for networks

    /**
     * Handles adding a new network to the project.
     * Prompts the user to choose between a standard or custom network.
     * If standard, calls the controller to add a standard network.
     * If custom, prompts for network details and calls the controller to add a custom network.
     */
    protected void handleAddNetwork() {
        ChoiceDialog<String> choice = new ChoiceDialog<>("Standard", "Standard", "Custom");
        choice.setTitle("Add Network");
        choice.setHeaderText("Choose network type:");
        choice.setContentText("Type:");

        Optional<String> result = choice.showAndWait();
        result.ifPresent(type -> {
            if (type.equals("Standard")) {
                controller.onClick("addStandardNetwork");
            } else {
                // Prompt for custom network info
                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setTitle("Custom Network");
                dialog.setHeaderText("Enter network details");

                ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField nameField = new TextField();
                TextField rangeField = new TextField();

                grid.add(new Label("Network name:"), 0, 0);
                grid.add(nameField, 1, 0);
                grid.add(new Label("Address range (e.g. 192.168.1.0/24):"), 0, 1);
                grid.add(rangeField, 1, 1);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == createButtonType) {
                        return new Pair<>(nameField.getText(), rangeField.getText());
                    }
                    return null;
                });

                Optional<Pair<String, String>> networkInfo = dialog.showAndWait();
                networkInfo.ifPresent(info -> {
                    controller.onClick("addCustomNetwork", info.getKey(), info.getValue());
                });
            }
        });
        this.view.updateDisplay();
    }

    protected void handleDeleteNetwork(TreeItem<String> networkItem) {
        String networkName = networkItem.getValue();
        controller.onClick("deleteNetwork", networkName);
        this.view.updateDisplay();
    }

    protected void handleEditeNetwork(TreeItem<String> networkItem) {
        Dialog<NetworkEditResult> dialog = new Dialog<>();
        dialog.setTitle("Edit Network");
        dialog.setHeaderText("Edit network details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField nameField = new TextField(networkItem.getValue());
        TextField addressField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Network name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Address Range:"), 0, 2);
        grid.add(addressField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nameField::requestFocus);
        
        // Convert result when Save is pressed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new NetworkEditResult(
                    nameField.getText(),
                    addressField.getText()
                );
            }
            return null;
        });

        Optional<NetworkEditResult> result = dialog.showAndWait();

        result.ifPresent(edited -> {
            controller.onClick("editNetwork",
                networkItem.getValue(), // old name
                edited.getName(),      // new name
                edited.getAddressRange()
            );
        });
        this.view.updateDisplay();
    }

    protected void handleAddDeviceHere(TreeItem<String> networkItem) {
        List<String> devices = model.getDeviceNames();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(devices.get(0), devices);
        dialog.setTitle("Assign to Network");
        dialog.setHeaderText("Choose a network:");

        dialog.showAndWait().ifPresent(device -> {
            controller.onClick("assignDeviceToNetwork", device, networkItem.getValue());
        });
        this.view.updateDisplay();
    }

    protected void handleMoveDevices(TreeItem<String> networkItem) {
        String item = networkItem.getValue();
        String[] parts = item.split(": ");
        String thisNetwork = parts[1];
        List<String> locations = new ArrayList<>(model.getNetworkNames());
        locations.add("unassigned");
        locations.remove(thisNetwork);
        ChoiceDialog<String> dialog = new ChoiceDialog<>(locations.get(0), locations);
        dialog.setTitle("Choose the location that has the devices you want in " + thisNetwork + ":");
        dialog.setHeaderText("Choose a location:");

        dialog.showAndWait().ifPresent(location -> {
            controller.onClick("moveDevices", thisNetwork, location);
        });
        this.view.updateDisplay();
    }

    //Misc handlers

    /**
     * Handles building the project.
     * Calls the controller to perform the build action which generates Dockerfiles for the devices and a docker-compose.
     */
    protected void handleBuildProject() {
        controller.onClick("buildProject");
    }
    //Helper methods and classes

    /**
     * Result class for the device edit dialog.
     * Contains the edited device details such as name, IP, OS, and services.
     */
    private class DeviceEditResult {
        private final String name, os, services, isEntryPoint;
    
        public DeviceEditResult(String name, String os, String services, String isEntryPoint) {
            this.name = name;
            this.os = os;
            this.services = services;
            this.isEntryPoint = isEntryPoint;
        }
    
        public String getName() { return name; }
        public String getOs() { return os; }
        public String getServices() { return services; }
        public String getIsEntryPoint() { return isEntryPoint; }
    }

    private class NetworkEditResult {
        private final String name, addressRange;
    
        public NetworkEditResult(String name, String addressRange) {
            this.name = name;
            this.addressRange = addressRange;
        }
    
        public String getName() { return name; }
        public String getAddressRange() { return addressRange; }
    }

    
}
