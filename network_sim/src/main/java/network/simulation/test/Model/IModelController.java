package network.simulation.test.Model;

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
     * adds a standard device to the model.
     * This method is typically used to add a predefined device configuration.
     */
    public void addStandardDevice();

    /**
     * Creates a device with the specified name and base image.
     * @param name  the name of the device to be created
     * @param baseImage the base image for the device, e.g., "ubuntu:latest"
     */
    public void createDevice(String name, String baseImage);

    /**
     * Deletes a device from the model.
     * This method removes the specified device from the home network or from unassigned.
     * @param device the name of the device to be deleted
     * @param home the name of where the device is located
     */
    public void deleteDevice(String device, String home);

    /**
     * Assigns a device to a specific network.
     * @param device the name of the device to be assigned
     * @param network the name of the network to which the device will be assigned
     */
    public void assignDevice(String device, String network);
    
    /**
     * Builds the project by writing Dockerfiles for each device and a docker-compose file for the network.
     * This method is typically called when the user is ready to deploy the network simulation.
     */
    public void buildProject();

}
