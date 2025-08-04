package network.simulation.test.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import network.simulation.test.Controller.IControllerView;
import network.simulation.test.Model.IModelView;
import network.simulation.test.Model.Nodes.Device;

public class MainView implements IView {
    private final BorderPane root;
    private final TreeView<String> projectTree;
    private final TreeItem<String> rootItem;
    private final VBox detailPanel = new VBox();
    private final Map<String, TreeItem<String>> networkItems = new HashMap<>();
    private ButtonHandler btnHandler;
    private IControllerView controller;
    private IModelView model;

    public MainView(IModelView model) {
        this.model = model;
        root = new BorderPane();

        VBox top = new VBox();           // Create top container
        createMenuBar(top);              // Build menu bar into this VBox
        root.setTop(top);                // Set it to the top of the layout

        rootItem = new TreeItem<>("Project: " + model.getName());
        rootItem.setExpanded(true);

        projectTree = new TreeView<>(rootItem);
        projectTree.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                displayDetailsFor(newItem);
            }
        });
        projectTree.setMinWidth(250);

        Button addNetworkBtn = new Button("Add Network");
        Button addDeviceBtn = new Button("Add Device");
        Button buildProjectBtn = new Button("Build");
        Button runProjectBtn = new Button("Run");

        addNetworkBtn.setOnAction(e -> this.btnHandler.handleAddNetwork());
        addDeviceBtn.setOnAction(e -> this.btnHandler.handleAddDevice());
        buildProjectBtn.setOnAction(e -> this.btnHandler.handleBuildProject());
        runProjectBtn.setOnAction(e -> runProject());

        VBox treeWithButtons = new VBox(projectTree, addNetworkBtn, addDeviceBtn, buildProjectBtn, runProjectBtn);
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
                    if (!item.startsWith("Network:") && !item.equals("Unassigned:")) {
                        setContextMenu(createDeviceContextMenu(getTreeItem()));
                    } else {
                        setContextMenu(createNetworkContextMenu(getTreeItem()));
                    }
                }
            }
        });

        root.setLeft(treeWithButtons);
        detailPanel.setPadding(new Insets(10));
        detailPanel.setSpacing(10);
        detailPanel.setPrefWidth(400);
        root.setRight(detailPanel);
        setControllerAndHandler(controller);
    }

    private void displayDetailsFor(TreeItem<String> selected) {
        detailPanel.getChildren().clear();
        String label = selected.getValue();
        ArrayList<String> info;
        if (label.startsWith("Network:")) {
            info = model.getNetworkInfo(label.substring(9));
            for (String item : info) {
                Label detailLabel = new Label(item);
                detailPanel.getChildren().add(detailLabel);
            }
        } else {
            info = model.getDeviceInfo(label);
            for (String item : info) {
                Label detailLabel = new Label(item);
                detailPanel.getChildren().add(detailLabel);
            }
        }
    }

    private void createMenuBar(VBox top) {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New Project");
        MenuItem openItem = new MenuItem("Open Project");
        MenuItem saveItem = new MenuItem("Save Project");
        newItem.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Project Directory");

            File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());

            if (selectedDirectory != null) {
                TextInputDialog dialog = new TextInputDialog("NewProject");
                dialog.setTitle("New Project");
                dialog.setHeaderText("Enter a name for the new project:");
                dialog.setContentText("Project name:");
    
                dialog.showAndWait().ifPresent(name -> controller.onClick("newProject", name, selectedDirectory.getAbsolutePath()));
            }
        });
        openItem.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open Project Directory");

            File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());

            if (selectedDirectory != null) {
                controller.onClick("openProject", selectedDirectory.getAbsolutePath());
            }
            updateDisplay();
        });
        saveItem.setOnAction(e -> {
            controller.onClick("saveProject");
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);

        // Edit menu
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(new MenuItem("Cut"), new MenuItem("Copy"), new MenuItem("Paste"));

        menuBar.getMenus().addAll(fileMenu, editMenu);

        top.getChildren().add(menuBar);
    }

    public Parent getView() {
        return root;
    }

    @Override
    public void setControllerAndHandler(IControllerView controller) {
        this.controller = controller;
        this.btnHandler = new ButtonHandler(model, this, this.controller);
    }

    @Override
    public void newProject(String name, String path) {
        model.setName(name);
        model.setPath(path);
        rootItem.setValue("Project: " + name);
        updateDisplay();
    }

    /**
     * Updates the display of the project tree view.
     * Clears the current items and repopulates it with networks and devices.
     * It creates a tree structure where each network is a parent item containing its devices as children.
     * Additionally, it includes an "Unassigned Devices" section for devices not assigned to any network.
     */
    protected void updateDisplay() {
        Set<String> currentNetworks = new HashSet<>(model.getNetworkNames());

        // Remove stale network items
        rootItem.getChildren().removeIf(item -> {
            String label = item.getValue();
            if (label.startsWith("Network: ")) {
                String name = label.substring("Network: ".length());
                return !currentNetworks.contains(name);
            }
            return false;
        });

        // Add or update networks
        for (String networkName : currentNetworks) {
            TreeItem<String> networkItem = networkItems.get(networkName);
            ArrayList<Device> devices = model.getDevicesInNetwork(networkName);

            if (networkItem == null) {
                networkItem = new TreeItem<>("Network: " + networkName);
                networkItems.put(networkName, networkItem);
                rootItem.getChildren().add(networkItem);
            }

            Set<String> currentDeviceNames = devices.stream()
                .map(Device::getName)
                .collect(Collectors.toSet());

            // Remove missing devices
            networkItem.getChildren().removeIf(child -> 
                !currentDeviceNames.contains(child.getValue()));

            // Add new devices
            for (Device device : devices) {
                boolean exists = networkItem.getChildren().stream()
                    .anyMatch(child -> child.getValue().equals(device.getName()));
                if (!exists) {
                    networkItem.getChildren().add(new TreeItem<>(device.getName()));
                }
            }
        }

        // Handle unassigned devices (optional: make it smart too)
        rootItem.getChildren().removeIf(item -> item.getValue().equals("Unassigned Devices"));
        TreeItem<String> unassignedItem = new TreeItem<>("Unassigned Devices");
        for (Device device : model.getUnassignedDevices()) {
            unassignedItem.getChildren().add(new TreeItem<>(device.getName()));
        }
        rootItem.getChildren().add(unassignedItem);
    }

    private ContextMenu createDeviceContextMenu(TreeItem<String> deviceItem) {
        MenuItem delete = new MenuItem("Delete Device");
        MenuItem edit = new MenuItem("Edit Device");
        MenuItem addToNetwork = new MenuItem("Add to Network");
        delete.setOnAction(e -> this.btnHandler.handleDeleteDevice(deviceItem));
        edit.setOnAction(e -> this.btnHandler.handleEditDevice(deviceItem));
        addToNetwork.setOnAction(e -> this.btnHandler.handleAddToNetwork(deviceItem));

        return new ContextMenu(delete, addToNetwork, edit);
    }

    private ContextMenu createNetworkContextMenu(TreeItem<String> networkItem) {
        MenuItem delete = new MenuItem("Delete Network");
        MenuItem edit = new MenuItem("Edit Network");
        MenuItem addDevice = new MenuItem("Add Device");
        MenuItem moveDevices = new MenuItem("Move Devices");

        delete.setOnAction(e -> this.btnHandler.handleDeleteNetwork(networkItem));
        edit.setOnAction(e -> this.btnHandler.handleEditeNetwork(networkItem));
        addDevice.setOnAction(e -> this.btnHandler.handleAddDeviceHere(networkItem));
        moveDevices.setOnAction(e -> this.btnHandler.handleMoveDevices(networkItem));
        return new ContextMenu(delete, edit, addDevice, moveDevices);
    }

    public void runProject() {
        RunPane runPane = new RunPane(this.model);
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(runPane, detailPanel);
        splitPane.setDividerPositions(0.7);  // 70% to RunPane, 30% to RightPane

        root.setCenter(splitPane);  // Set the SplitPane as the only center node
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Path path = Path.of(model.getPath());
                ProcessBuilder pb = new ProcessBuilder("docker-compose", "up", "--build", "-d");
                pb.directory(path.toFile());
                pb.redirectErrorStream(true);

                try {
                    Process process = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> runPane.appendLog(finalLine + "\n"));
                    }
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        Platform.runLater(runPane::markComplete);
                    } else {
                        Platform.runLater(() -> runPane.markError("Docker exited with code " + exitCode + "\n"));
                    }
                } catch (IOException | InterruptedException e) {
                    Platform.runLater(() -> runPane.markError("Error: " + e.getMessage() + "\n"));
                }

                return null;
            }
        };

        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }


    @Override
    public void closeProject() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeProject'");
    }

    
}