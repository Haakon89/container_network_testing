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
     * Sets the name of the current project.
     * @param name the new name for the project
     */
    public void setName(String name);

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

    /**
     * Returns a list of devices in a specific network.
     * @param networkName the name of the network to search in
     * @return an ArrayList of devices in the specified network, or an empty list if the network does not exist
     */
    public ArrayList<Device> getDevicesInNetwork(String name);

    /**
     * Finds a device by its name and returns the network it belongs to.
     * If the device is unassigned, it returns "unassigned".
     * @param name the name of the device to find
     * @return the name of the network the device belongs to, or "unassigned" if it is not assigned to any network
     */
    public String findDevice(String deviceName);

    /**
     * find all relevant information about a network
     * @param name the name of the network to search for
     * @return an ArrayList of strings containing information about the network
     */
    public ArrayList<String> getNetworkInfo(String name);

    /**
     * find all relevant information about a device
     * @param name the name of the device to search for
     * @return an ArrayList of strings containing information about the device
     */
    public ArrayList<String> getDeviceInfo(String name);
    
}
