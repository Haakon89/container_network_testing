package network.simulation.test.View;

import network.simulation.test.Controller.IControllerView;

public interface IView {

    /**
     * Creates a new project with the given name.
     * @param name the name of the new project
     * @param path the path where the project will be saved
     */
    public void newProject(String name, String path);

    /**
     * Closes the current project.
     * @param name the name of the project to be closed
     */
    public void closeProject();

    /**
     * Sets the main controller for the view.
     * This allows the view to communicate with the controller for actions like creating networks, devices, etc.
     * @param mainController the controller that will handle user interactions
     */
    public void setController(IControllerView mainController);
}
