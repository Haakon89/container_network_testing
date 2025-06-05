package network.simulation.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.simulation.test.Controller.MainController;
import network.simulation.test.View.MainView;

public class AppLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView view = new MainView();
        MainController controller = new MainController();

        Scene scene = new Scene(view.getView(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
