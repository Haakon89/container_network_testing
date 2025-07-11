package network.simulation.test.View;

import java.nio.file.Path;
import java.util.*;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import network.simulation.test.Controller.IViewController;
import network.simulation.test.Model.Model;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.UtilityClasses.Tuple;

public class MainView implements IView {
    private final BorderPane root;
    private final TreeView<String> projectTree;
    private final TreeItem<String> rootItem;
    private final Map<String, TreeItem<String>> networkItems = new HashMap<>();

    private IViewController controller;
    private Model model;

    public MainView(Model model) {
        this.model = model;
        root = new BorderPane();

        VBox top = new VBox();           // Create top container
        createMenuBar(top);              // Build menu bar into this VBox
        root.setTop(top);                // Set it to the top of the layout

        rootItem = new TreeItem<>("Project: " + model.getName());
        rootItem.setExpanded(true);

        projectTree = new TreeView<>(rootItem);
        projectTree.setMinWidth(250);

        Button addNetworkBtn = new Button("Add Network");
        Button addDeviceBtn = new Button("Add Device");
        Button buildProjectBtn = new Button("Build");

        addNetworkBtn.setOnAction(e -> handleAddNetwork());
        addDeviceBtn.setOnAction(e -> handleAddDevice());
        buildProjectBtn.setOnAction(e -> handleBuildProject());

        VBox treeWithButtons = new VBox(projectTree, addNetworkBtn, addDeviceBtn, buildProjectBtn);
        treeWithButtons.setPadding(new Insets(10));
        treeWithButtons.setSpacing(5);

        projectTree.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setContextMenu(null);
                } else {
                    setText(item);
                    if (!item.startsWith("Network:") && !item.equals("Unassigned Nodes")) {
                        setContextMenu(createDeviceContextMenu(getTreeItem()));
                    } else {
                        setContextMenu(null);
                    }
                }
            }
        });

        root.setLeft(treeWithButtons);
        setController(controller);
    }

    /**
     * Handles adding a new device to the project.
     * Prompts the user to choose between a standard or custom device.
     * If standard, calls the controller to add a standard device.
     * If custom, prompts for device details and calls the controller to add a custom device.
     */
    private void handleAddDevice() {
        ChoiceDialog<String> choice = new ChoiceDialog<>("Standard", "Standard", "Custom");
        choice.setTitle("Add Device");
        choice.setHeaderText("Choose device type:");
        choice.setContentText("Type:");

        Optional<String> result = choice.showAndWait();
        result.ifPresent(type -> {
            if (type.equals("Standard")) {
                controller.onClick("addStandardDevice");
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
                    controller.onClick("addCustomDevice", info.getKey(), info.getValue());
                });
            }
        });
        updateDisplay();
    }

    /**
     * Handles adding a new network to the project.
     * Prompts the user to choose between a standard or custom network.
     * If standard, calls the controller to add a standard network.
     * If custom, prompts for network details and calls the controller to add a custom network.
     */
    private void handleAddNetwork() {
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
        updateDisplay();
    }

    /**
     * Handles building the project.
     * Calls the controller to perform the build action which generates Dockerfiles for the devices and a docker-compose.
     */
    private void handleBuildProject() {
        controller.onClick("buildProject");
    }

    private void createMenuBar(VBox top) {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New Project");
        newItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("NewProject");
            dialog.setTitle("New Project");
            dialog.setHeaderText("Enter a name for the new project:");
            dialog.setContentText("Project name:");

            dialog.showAndWait().ifPresent(name -> controller.onClick("newProject", name));
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(newItem, exitItem);

        // Edit menu
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(new MenuItem("Cut"), new MenuItem("Copy"), new MenuItem("Paste"));

        menuBar.getMenus().addAll(fileMenu, editMenu);

        // ✅ This is the ONLY correct way now
        top.getChildren().add(menuBar);
    }

    public Parent getView() {
        return root;
    }

    @Override
    public void setController(IViewController controller) {
        this.controller = controller;
    }

    @Override
    public void newProject(String name) {
        model.setName(name);
        rootItem.setValue("Project: " + name);
        updateDisplay();
    }

    /**
     * Updates the display of the project tree view.
     * Clears the current items and repopulates it with networks and devices.
     * It creates a tree structure where each network is a parent item containing its devices as children.
     * Additionally, it includes an "Unassigned Devices" section for devices not assigned to any network.
     */
    private void updateDisplay() {
        rootItem.getChildren().clear();
        networkItems.clear();

        // Networks
        for (String name : model.getNetworkNames()) {
            ArrayList<Device> devices = model.getDevicesInNetwork(name);

            TreeItem<String> networkItem = new TreeItem<>("Network: " + name);
            for (Device device : devices) {
                TreeItem<String> deviceItem = new TreeItem<>(device.getName());
                networkItem.getChildren().add(deviceItem);
            }
            networkItems.put(name, networkItem);
            rootItem.getChildren().add(networkItem);
        }

        // Unassigned
        TreeItem<String> unassignedItem = new TreeItem<>("Unassigned Devices");
        for (Device device : model.getUnassignedDevices()) {
            TreeItem<String> deviceItem = new TreeItem<>(device.getName());
            unassignedItem.getChildren().add(deviceItem);
        }
        rootItem.getChildren().add(unassignedItem);
    }

    private ContextMenu createDeviceContextMenu(TreeItem<String> deviceItem) {
        MenuItem delete = new MenuItem("Delete Device");
        MenuItem edit = new MenuItem("Edit Device");
        MenuItem addToNetwork = new MenuItem("Add to Network");
        delete.setOnAction(e -> handleDeleteDevice(deviceItem));
        edit.setOnAction(e -> handleEditDevice(deviceItem));
        addToNetwork.setOnAction(e -> handleAddToNetwork(deviceItem));

        return new ContextMenu(delete, addToNetwork, edit);
    }

    /**
     * Result class for the device edit dialog.
     * Contains the edited device details such as name, IP, OS, and services.
     */
    public class DeviceEditResult {
        private final String name, ip, os, services;
    
        public DeviceEditResult(String name, String ip, String os, String services) {
            this.name = name;
            this.ip = ip;
            this.os = os;
            this.services = services;
        }
    
        public String getName() { return name; }
        public String getIp() { return ip; }
        public String getOs() { return os; }
        public String getServices() { return services; }
    }

    /**
     * Handles editing a device.
     * Opens a dialog to edit the device details such as name, IP, OS, and services.
     * On saving, it calls the controller to update the device and refreshes the display.
     *
     * @param deviceItem The TreeItem representing the device to be edited.
     */
    private void handleEditDevice(TreeItem<String> deviceItem) {
        Dialog<DeviceEditResult> dialog = new Dialog<>();
        dialog.setTitle("Edit Device");
        dialog.setHeaderText("Edit device details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField nameField = new TextField(deviceItem.getValue());
        TextField ipField = new TextField();
        TextField osField = new TextField();
        TextField servicesField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Device name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("IP address:"), 0, 1);
        grid.add(ipField, 1, 1);
        grid.add(new Label("Operating System:"), 0, 2);
        grid.add(osField, 1, 2);
        grid.add(new Label("Services (comma-separated):"), 0, 3);
        grid.add(servicesField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nameField::requestFocus);

        // Convert result when Save is pressed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new DeviceEditResult(
                    nameField.getText(),
                    ipField.getText(),
                    osField.getText(),
                    servicesField.getText()
                );
            }
            return null;
        });

        Optional<DeviceEditResult> result = dialog.showAndWait();

        result.ifPresent(edited -> {
            controller.onClick("editDevice",
                deviceItem.getValue(), // old name
                edited.getName(),      // new name
                edited.getIp(),
                edited.getOs(),
                edited.getServices()
            );
            updateDisplay();
        });
    }

    /**
     * Handles adding a device to a network.
     * Prompts the user to choose a network from the available networks.
     * On selection, it calls the controller to assign the device to the selected network and refreshes the display.
     *
     * @param deviceItem The TreeItem representing the device to be added to a network.
     */
    private void handleAddToNetwork(TreeItem<String> deviceItem) {
        List<String> networks = model.getNetworkNames();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(networks.get(0), networks);
        dialog.setTitle("Assign to Network");
        dialog.setHeaderText("Choose a network:");

        dialog.showAndWait().ifPresent(network -> {
            controller.onClick("assignDeviceToNetwork", deviceItem.getValue(), network);
            updateDisplay();
        });
    }

    /**
     * Handles deleting a device from the project.
     * Calls the controller to delete the device and refreshes the display.
     *
     * @param deviceItem The TreeItem representing the device to be deleted.
     */
    private void handleDeleteDevice(TreeItem<String> deviceItem) {
        String deviceName = deviceItem.getValue();
        String deviceHome = model.findDevice(deviceName);
        controller.onClick("deleteDevice", deviceName, deviceHome);
        updateDisplay();
    }

    @Override
    public void openProject(Path path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openProject'");
    }

    @Override
    public void saveProject() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveProject'");
    }

    @Override
    public void saveProjectAs(String name, Path path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveProjectAs'");
    }

    @Override
    public void closeProject() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeProject'");
    }

    
}