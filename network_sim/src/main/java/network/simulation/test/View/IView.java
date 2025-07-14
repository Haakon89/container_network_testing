package network.simulation.test.View;

import java.nio.file.Path;

import network.simulation.test.Controller.IViewController;

public interface IView {

    /**
     * Creates a new project with the given name.
     * @param name the name of the new project
     */
    public void newProject(String name);

    /**
     * Opens an existing project from the specified path.
     * @param path the path to the project file
     */
    public void openProject(String path);

    /**
     * Saves the current project to its current location.
     */
    public void saveProject();

    /**
     * Saves the current project with a new name and path.
     * @param name the new name for the project
     * @param path the new path where the project will be saved
     */
    public void saveProjectAs(String name, String path);

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
    public void setController(IViewController mainController);
}
