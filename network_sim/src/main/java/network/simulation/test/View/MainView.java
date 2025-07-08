package network.simulation.test.View;

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

        addNetworkBtn.setOnAction(e -> handleAddNetwork());
        addDeviceBtn.setOnAction(e -> handleAddDevice());

        VBox treeWithButtons = new VBox(projectTree, addNetworkBtn, addDeviceBtn);
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

    private void handleEditDevice(TreeItem<String> deviceItem) {
        TextInputDialog dialog = new TextInputDialog(deviceItem.getValue());
        dialog.setTitle("Edit Device");
        dialog.setHeaderText("Rename Device");
        dialog.setContentText("New name:");

        dialog.showAndWait().ifPresent(newName -> {
            controller.onClick("editDevice", deviceItem.getValue(), newName);
            deviceItem.setValue(newName);
        });
    }

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

    private void handleDeleteDevice(TreeItem<String> deviceItem) {
        String deviceName = deviceItem.getValue();
        for (Device device : model.getUnassignedDevices()) {
            if (device.getName().equals(deviceName)) {
                controller.onClick("deleteDevice", deviceName, "unassigned");
                updateDisplay();
                return;
            }
        }
        ArrayList<String> networkNames = model.getNetworkNames();
        for (String name : networkNames) {
            for (Device device : model.getDevicesInNetwork(name)) {
                if (device.getName().equals(deviceName)) {
                    controller.onClick("deleteDevice", deviceName, name);
                    updateDisplay();
                    return;
                }
            }
        }
    }

    @Override
    public void openProject(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openProject'");
    }

    @Override
    public void saveProject(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveProject'");
    }

    @Override
    public void saveProjectAs(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveProjectAs'");
    }

    @Override
    public void closeProject(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeProject'");
    }

    
}