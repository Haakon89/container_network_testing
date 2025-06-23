package network.simulation.test.View;

import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import network.simulation.test.Controller.IViewController;
import network.simulation.test.Model.Model;

public class MainView implements IView {
    private BorderPane root;
    private IViewController controller;
    private Model model;
    private TreeView<String> projectTree;
    private TreeItem<String> rootItem;


    public MainView(Model model) {
        this.model = model;
        root = new BorderPane();

        VBox top = new VBox(createMenuBar());
        root.setTop(top);

        rootItem = new TreeItem<>("Project: " + model.getName());
        rootItem.setExpanded(true);
        projectTree = new TreeView<>(rootItem);
        projectTree.setMinWidth(250);

        TreeItem<String> unassigned = new TreeItem<>("Unassigned Nodes");
        rootItem.getChildren().add(unassigned);

        Button addNetworkBtn = new Button("Add Network");
        Button addNodeBtn = new Button("Add Node");

        addNetworkBtn.setOnAction(e -> handleAddNetwork());
        addNodeBtn.setOnAction(e -> handleAddNode());

        VBox treeWithButtons = new VBox(projectTree, addNetworkBtn, addNodeBtn);
        treeWithButtons.setPadding(new Insets(10));
        treeWithButtons.setSpacing(5);

        root.setLeft(treeWithButtons);
        setController(controller);
    }

    private void initProjectTree(String projectName) {
        rootItem = new TreeItem<>("Project: " + projectName);
        rootItem.setExpanded(true);
    
        TreeItem<String> unassigned = new TreeItem<>("Unassigned Nodes");
        rootItem.getChildren().add(unassigned);
    
        projectTree.setRoot(rootItem);
    }

    private MenuBar createMenuBar() {
        // build menu bar
        MenuBar menuBar = new MenuBar();
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("NewProject");
            dialog.setTitle("New Project");
            dialog.setHeaderText("Enter a name for the new project:");
            dialog.setContentText("Project name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> controller.onClick("newProject", name));
        });

        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");

        exitItem.setOnAction(e -> Platform.exit()); // or primaryStage.close()

        fileMenu.getItems().addAll(newItem, openItem, saveItem, new SeparatorMenuItem(), exitItem);

        // Edit Menu
        Menu editMenu = new Menu("Edit");

        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");

        editMenu.getItems().addAll(cutItem, copyItem, pasteItem);

        // Add Menus to MenuBar
        menuBar.getMenus().addAll(fileMenu, editMenu);
        return menuBar;
    }

    public Parent getView() {
        return root;
    }

    @Override
    public void addNetworkToTree(String networkName) {
        TreeItem<String> networkItem = new TreeItem<>("Network: " + networkName);
        rootItem.getChildren().add(networkItem);
    }

    @Override
    public void addNodeToTree(String nodeName) {
        TreeItem<String> nodeItem = new TreeItem<>(nodeName);
        nodeItem.setContextMenu(createNodeContextMenu(nodeItem));

        for (TreeItem<String> child : rootItem.getChildren()) {
            if (child.getValue().equals("Unassigned Nodes")) {
                child.getChildren().add(nodeItem);
                return;
            }
        }
        
    }

    private ContextMenu createNodeContextMenu(TreeItem<String> nodeItem) {
        MenuItem delete = new MenuItem("Delete Node");
        MenuItem edit = new MenuItem("Edit Node");
        MenuItem addToNetwork = new MenuItem("Add to Network");

        delete.setOnAction(e -> handleDeleteNode(nodeItem));
        edit.setOnAction(e -> handleEditNode(nodeItem));
        addToNetwork.setOnAction(e -> handleAddToNetwork(nodeItem));

        return new ContextMenu(delete, addToNetwork, edit);
    }

    private void handleDeleteNode(TreeItem<String> nodeItem) {
        TreeItem<String> parent = nodeItem.getParent();
        if (parent != null) {
            parent.getChildren().remove(nodeItem);
            controller.onClick("deleteNode", nodeItem.getValue());
        }
    }

    private void handleEditNode(TreeItem<String> nodeItem) {
        TextInputDialog dialog = new TextInputDialog(nodeItem.getValue());
        dialog.setTitle("Edit Node");
        dialog.setHeaderText("Rename node");
        dialog.setContentText("New name:");
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            controller.onClick("editNode", nodeItem.getValue(), newName);
            nodeItem.setValue(newName);
        });
    }

    private void handleAddToNetwork(TreeItem<String> nodeItem) {
        List<String> availableNetworks = model.getNetworks(); // Implement this
        ChoiceDialog<String> dialog = new ChoiceDialog<>(availableNetworks.get(0), availableNetworks);
        dialog.setTitle("Add to Network");
        dialog.setHeaderText("Select a network to add the node to");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(networkName -> {
            controller.onClick("assignNodeToNetwork", nodeItem.getValue(), networkName);

            // Move node visually
            TreeItem<String> networkItem = networkItems.get(networkName); // You should be storing these
            if (networkItem != null) {
                nodeItem.getParent().getChildren().remove(nodeItem);
                networkItem.getChildren().add(nodeItem);
            }
        });
    }

    public void displayModel() {
        // This method can be used to update the view with the current model state
        // For now, we will just print the model name to the console
        if (model != null) {
            System.out.println("Current Model: " + model.getName());
        } else {
            System.out.println("No model loaded.");
        }
    }

    @Override
    public void newProject(String name) {
        this.model.setName(name);

        // Rebuild tree structure but preserve the UI wrapper (VBox)
        initProjectTree(name);

        displayModel();
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

    @Override
    public void setController(IViewController mainController) {
        this.controller = mainController;
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
                // Prompt for name and address range
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
    }

    private void handleAddNode() {
        ChoiceDialog<String> choice = new ChoiceDialog<>("Standard", "Standard", "Custom");
        choice.setTitle("Add Node");
        choice.setHeaderText("Choose node type:");
        choice.setContentText("Type:");
    
        Optional<String> result = choice.showAndWait();
        result.ifPresent(type -> {
            if (type.equals("Standard")) {
                controller.onClick("addStandardNode");
            } else {
                // Prompt for name and base image
                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setTitle("Custom Node");
                dialog.setHeaderText("Enter node details");
    
                ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
    
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));
    
                TextField nameField = new TextField();
                TextField imageField = new TextField();
    
                grid.add(new Label("Node name:"), 0, 0);
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
    
                Optional<Pair<String, String>> nodeInfo = dialog.showAndWait();
                nodeInfo.ifPresent(info -> {
                    controller.onClick("addCustomNode", info.getKey(), info.getValue());
                });
            }
        });
    }


}
