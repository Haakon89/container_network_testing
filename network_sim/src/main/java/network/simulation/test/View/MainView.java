package network.simulation.test.View;

import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class MainView {
    private BorderPane root;

    public MainView() {
        // Create top menu bar etc.
        root = new BorderPane();
        root.setTop(createMenuBar());
    }

    private MenuBar createMenuBar() {
        // build menu bar
        return new MenuBar();
    }

    public Parent getView() {
        return root;
    }
}
