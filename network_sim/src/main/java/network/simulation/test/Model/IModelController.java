package network.simulation.test.Model;

import java.util.List;

import network.simulation.test.Model.Nodes.Device;

public interface IModelController {

    /**
     * Adds a network to the model with a set of standard parameters.
     * This method is typically used to add a predefined network configuration.
     */
    public void addStandardNetwork();

    /**
     * Creates a custom network with the specified name and address.
     * @param name the name of the custom network
     * @param addressRange the address range for the custom network, e.g., "
     */
    public void createNetwork(String name, String addressRange);

    /**
     * creates a new device of a given type
     * @param deviceType the type of device we want to create
     */
    public void createDevice(String deviceType);

    /**
     * Creates a device with the specified name and base image.
     * @param name  the name of the device to be created
     * @param baseImage the base image for the device, e.g., "ubuntu:latest"
     */
    public void createCustomDevice(String name, String baseImage);

    /**
     * Deletes a device from the model.
     * This method removes the specified device from the home network or from unassigned.
     * @param device the name of the device to be deleted
     * @param home the name of where the device is located
     */
    public void deleteDevice(String device, String home);

    /**
     * deletes a network and all its related devices
     * @param networkName name of the network to be deleted
     */
    public void deleteNetwork(String networkName);

    /**
     * Assigns a device to a specific network.
     * @param device the name of the device to be assigned
     * @param network the name of the network to which the device will be assigned
     */
    public void assignDevice(String device, String network);

    /**
     * moves all devices from one place to another
     * @param destination where the devices will end up
     * @param origin where the devices are coming from
     */
    public void moveDevices(String destination, String origin);
    
    /**
     * Builds the project by writing Dockerfiles for each device and a docker-compose file for the network.
     * This method is typically called when the user is ready to deploy the network simulation.
     */
    public void buildProject();

    /**
     * gets all the devices in the model
     * @return a list of all devices in the model
     */
    public List<Device> getAllDevices();

    /**
     * Edits the details of an existing device.
     * @param oldName the current name of the device to be edited
     * @param newName the new name for the device
     * @param newOS the new operating system for the device
     * @param newServices the new services for the device, as a comma-separated string
     * @param entryPoint indicates whether the device is an entry point (true or false)
     */
    public void editDevice(String oldName, String newName, String newOS, String newServices, String entryPoint);

    /**
     * Edits the information of a network and updates all devices in the network
     * @param oldName the old name of the network
     * @param newName a new name if any
     * @param newAddressRange new address range if any
     */
    public void editNetwork(String oldName, String newName, String newAddressRange);

    /**
     * loads the model from a given path
     * @param path the path to the model file
     */
    public void loadModel(String path);

    /**
     * Saves the current model to the path set at the model's construction.
     */
    public void saveModel();

    /**
     * Saves the current model to a specified path with a given name.
     * This method allows the user to save the model under a different name or location.
     * @param name the name of the model to be saved
     * @param path the path where the model will be saved
     */
    public void saveModelAs(String name, String path);

    

    

    
    
    
}
