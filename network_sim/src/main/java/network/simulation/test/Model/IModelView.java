package network.simulation.test.Model;

import java.util.ArrayList;

import network.simulation.test.Model.Nodes.Device;

public interface IModelView {

    /**
     * Gets the name of the current project.
     * @return the name of the project
     */
    public String getName();

    /**
     * Gets the names of all networks in the model.
     * @return a list of network names
     */
    public ArrayList<String> getNetworkNames();
    
    /**
     * Gets the names of all unassigned devices in the model.
     * @return a list of unassigned devices
     */
    public ArrayList<Device> getUnassignedDevices();
    
}
