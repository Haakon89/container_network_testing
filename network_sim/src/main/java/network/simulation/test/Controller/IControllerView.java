package network.simulation.test.Controller;

import network.simulation.test.View.RunPane;

public interface IControllerView {
    /**
     * Handles click events from the view.
     * @param action the action to perform, such as "newProject", "addStandardNetwork", etc.
     * @param args optional arguments for the action, such as project name, network name, etc.
     */
    public void onClick(String action, String... args);
}
