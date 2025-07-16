package network.simulation.test.View;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class RunPane extends VBox {
    private ProgressBar progressBar;
    private final Label statusLabel;
    private TextArea outputArea;

    public RunPane() {
        setSpacing(10);
        setPadding(new Insets(10));

        statusLabel = new Label("Building Docker Environment...");

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); // Spinner-style

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        getChildren().addAll(statusLabel, progressBar, outputArea);
    }

    public void appendLog(String text) {
        outputArea.appendText(text);
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public void bindToTask(Task<Void> task) {
        progressBar.progressProperty().bind(task.progressProperty());
    }

    public void markComplete() {
        progressBar.setProgress(1.0);  // Optional: can also use setVisible(false)
        statusLabel.setText("Docker setup complete.");
    }

    public void markError(String error) {
        progressBar.setProgress(0);
        statusLabel.setText("Error: " + error);
    }
    
}
