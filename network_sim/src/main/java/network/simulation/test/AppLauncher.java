package network.simulation.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.simulation.test.Controller.MainController;
import network.simulation.test.Model.Model;
import network.simulation.test.View.MainView;

public class AppLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        Model model = new Model();
        MainView view = new MainView(model);
        MainController controller = new MainController(model, view);

        Scene scene = new Scene(view.getView(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
