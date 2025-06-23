package network.simulation.test.View;

import network.simulation.test.Controller.IViewController;

public interface IView {
    public void newProject(String name);
    public void openProject(String name);
    public void saveProject(String name);
    public void saveProjectAs(String name);
    public void closeProject(String name);
    public void setController(IViewController mainController);
    public void addNetworkToTree(String networkName);
    public void addNodeToTree(String nodeName);
}
