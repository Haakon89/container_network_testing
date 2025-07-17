package network.simulation.test.View;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import network.simulation.test.Model.IModelView;

public class RunPane extends VBox {
    private ProgressBar progressBar;
    private final Label statusLabel;
    private TextArea outputArea;
    private Button accessEntryPointBtn;
    private Button shutdownBtn;;
    private IModelView model;

    public RunPane(IModelView model) {
        this.model = model;
        setSpacing(10);
        setPadding(new Insets(10));

        statusLabel = new Label("Building Docker Environment...");

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); // Spinner-style

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        accessEntryPointBtn = new Button("Access Entry-Point");
        accessEntryPointBtn.setVisible(false);
        accessEntryPointBtn.setOnAction(e -> handleAccessEntryPoint());
        shutdownBtn = new Button("Shutdown");
        shutdownBtn.setVisible(false);
        shutdownBtn.setOnAction(e -> handleShutdown());
        VBox layout = new VBox(statusLabel, progressBar, outputArea, accessEntryPointBtn, shutdownBtn);
        getChildren().add(layout);
    }

    private void handleShutdown() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        ProcessBuilder pb = new ProcessBuilder("docker-compose", "down");
        pb.directory(Paths.get(model.getPath()).toFile());
        try {
            Process process = pb.start();
            process.waitFor();
            outputArea.appendText("Docker environment shut down successfully.\n");
            markShutdownComplete();
        } catch (IOException | InterruptedException e) {
            outputArea.appendText("Error shutting down Docker environment: " + e.getMessage() + "\n");
            markError(e.getMessage());
        }
    }

    private void handleAccessEntryPoint() {
        String entryContainer = model.getEntryPoint();
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb = null;

        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", "docker exec -it " + entryContainer + " bash");
        } else if (os.contains("mac")) {
            pb = new ProcessBuilder("osascript", "-e",
                "tell application \"Terminal\" to do script \"docker exec -it " + entryContainer + " bash\"");
        } else if (os.contains("nix") || os.contains("nux")) {
            pb = new ProcessBuilder("x-terminal-emulator", "-e", "docker", "exec", "-it", entryContainer, "bash");
        }

        try {
            if (pb != null) pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendLog(String text) {
        outputArea.appendText(text);
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public void markComplete() {
        progressBar.setProgress(1.0);  // Optional: can also use setVisible(false)
        statusLabel.setText("Docker setup complete.");
        if (model.getEntryPoint() != null) {
            accessEntryPointBtn.setVisible(true);
            shutdownBtn.setVisible(true);
        }
    }

    public void markShutdownComplete() {
        progressBar.setProgress(1);
        statusLabel.setText("Docker environment shut down.");
        accessEntryPointBtn.setVisible(false);
    }

    public void markError(String error) {
        progressBar.setProgress(0);
        statusLabel.setText("Error: " + error);
    }
    
}
