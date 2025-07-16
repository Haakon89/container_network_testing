package network.simulation.test.View;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class RunPane extends VBox {
    private ProgressBar progressBar;
    private TextArea outputArea;

    public RunPane() {
        setPadding(new Insets(10));
        setSpacing(10);

        Label title = new Label("Running Docker Setup...");
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        getChildren().addAll(title, progressBar, outputArea);
    }

    public void appendLog(String text) {
        outputArea.appendText(text);
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }
    
}
