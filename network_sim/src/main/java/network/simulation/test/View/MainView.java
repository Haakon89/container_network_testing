package network.simulation.test.View;

import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import network.simulation.test.Controller.IViewController;
import network.simulation.test.Controller.MainController;
import network.simulation.test.Model.Model;

public class MainView implements IView {
    private BorderPane root;
    private IViewController controller;
    private Model model;
    private TreeView<String> projectTree;
    private TreeItem<String> rootItem;


    public MainView() {
        // Create top menu bar etc.
        root = new BorderPane();
        
        VBox top = new VBox();
        top.getChildren().add(createMenuBar());
        root.setTop(top);

        initProjectTree();
        root.setLeft(projectTree);

        setController(controller);
    }

    private void initProjectTree() {
        rootItem = new TreeItem<>("No Project");
        rootItem.setExpanded(true);
        projectTree = new TreeView<>(rootItem);
        projectTree.setMinWidth(250); // Optional width setting
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
        this.model = new Model(name);
        rootItem.setValue("Project: " + name);
        rootItem.getChildren().clear();

        TreeItem<String> unassigned = new TreeItem<>("Unassigned Nodes");
        rootItem.getChildren().add(unassigned);

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

    
}
