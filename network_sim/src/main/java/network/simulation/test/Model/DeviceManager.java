package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Nodes.CustomDevice;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.DeviceFactory;
import network.simulation.test.Model.Nodes.RouterDevice;

public class DeviceManager {
    private Model model;

    public DeviceManager(Model model) {
        this.model = model;
    }

    /**
     * Creates a custom device with the specified name and base image.
     * @param name the name of the device
     * @param baseImage the base image for the device
     */
    public void createCustomDevice(String name, String baseImage) {
        String lowerCaseName = name.toLowerCase();
        Device device = new CustomDevice(lowerCaseName, baseImage);
        model.unassignedDevices.add(device);
        model.devices.put(lowerCaseName, device);
        model.deviceNames.add(name);
        model.devicesCreated++;
    }

    /**
     * Creates a device of the specified type.
     * The type must be one of the predefined device types in DeviceFactory.
     * @param deviceType the type of the device to create
     */
    public void createDevice(String deviceType) {
        Device newDevice = DeviceFactory.buildDevice(deviceType);
        String name = newDevice.getName();
        if (deviceType.equals("router")) {
            model.routers.add((RouterDevice) newDevice);
        }
        model.unassignedDevices.add(newDevice);
        model.devices.put(name, newDevice);
        model.deviceNames.add(name);
        model.devicesCreated++;
    }

    /**
     * Deletes a device by its name from the specified network or from unassigned devices.
     * @param name the name of the device to delete
     * @param home the network name where the device is located, or "unassigned" if it is not assigned to any network
     */
    public void deleteDevice(String name, String home) {
        ArrayList<Device> unassignedDevices = model.getUnassignedDevices();
        if (home.equals("unassigned")) {
            for (Device device : unassignedDevices) {
                if (device.getName().equals(name)) {
                    unassignedDevices.remove(device);
                    model.devices.remove(name);
                    return;
                }
            }
        } else {
            HashMap<String, Network> networks = model.getNetworks();
            Network network = networks.get(home);
            if (network != null) {
                Device deviceToRemove = null;
                for (Device device : network.getDevicesInNetwork()) {
                    if (device.getName().equals(name)) {
                        deviceToRemove = device;
                        break;
                    }
                }
                if (deviceToRemove != null) {
                    network.removeDevice(deviceToRemove);
                    model.devices.remove(name);
                    model.deviceNames.remove(name);
                    model.devicesCreated--;
                } else {
                    System.out.println("device " + name + " not found in network " + home + ".");
                }
            } else {
                System.out.println("Network " + home + " not found.");
            }
        }
    }

    /**
     * Assigns a device to a network.
     * @param device the name of the device to assign
     * @param network the name of the network to assign the device to
     */
    public void assignDevice(String device, String network) {
        Device deviceToAssigne = model.devices.get(device);
        if (deviceToAssigne instanceof RouterDevice) {
            ((RouterDevice) deviceToAssigne).addNetworkTorouter(model.networks.get(network));
        } else {
            model.networks.get(network).addDevice(deviceToAssigne);
        }
        model.unassignedDevices.remove(deviceToAssigne);
    }
    
    /**
     * Finds a device by its name and returns the network it belongs to.
     * If the device is unassigned, it returns "unassigned".
     * @param name the name of the device to find
     * @return the name of the network the device belongs to, or "unassigned" if it is not assigned to any network
     */
    public String findDevice(String name) {
        ArrayList<Device> unassignedDevices = model.getUnassignedDevices();
        Device deviceToFind = model.devices.get(name);
        HashMap<String, Network> networks = model.getNetworks();
        if (unassignedDevices.contains(deviceToFind)) {
            return "unassigned";
        }
        for (Network network : networks.values()) {
            ArrayList<Device> devicesInNetwork = network.getDevicesInNetwork();
            if (devicesInNetwork.contains(deviceToFind)) {
                return network.getName();
            }
        }
        return null; // Device not found
    }

    /**
     * Edits the properties of a device.
     * @param oldName the current name of the device
     * @param newName the new name for the device, can be null to keep the same name
     * @param newOS the new base image for the device, can be null to keep the same image
     * @param newServices a comma-separated list of services to install on the device, can be null to keep the same services
     * @param entryPoint whether the device should be set as an entry point, can be null to keep the current setting
     */
    public void editDevice(String oldName, String newName, String newOS, String newServices, String entryPoint) {
        Device device = model.devices.get(oldName);
        if (newName != null && !newName.isEmpty()) {
            device.setName(newName);
        }
        if (newOS != null && !newOS.isEmpty()) {
            device.setBaseImage(newOS);
        } 
        if (newServices != null && !newServices.isEmpty()) {
            String[] servicesArray = newServices.split(",");
            for (String service : servicesArray) {
                device.installService(service.trim());
            }
        }
        if (entryPoint != null && !entryPoint.isEmpty()) {
            boolean isEntryPoint = Boolean.parseBoolean(entryPoint);
            device.setEntryPoint(isEntryPoint);
            if (isEntryPoint) {
                model.setEntryPoint(device.getName());
            } else if (model.getEntryPoint().equals(device.getName())) {
                model.setEntryPoint(null);  
            }
        }
    }

    /**
     * Returns a list of devices in the specified network.
     * @param networkName the name of the network to get devices from
     * @return an ArrayList of devices in the specified network, or an empty list if the network does not exist
     */
    public ArrayList<Device> getDevicesInNetwork(String networkName) {
        HashMap<String, Network> networks = model.getNetworks();
        Network network = networks.get(networkName);
        if (network != null) {
            return network.getDevicesInNetwork();
        } else {
            System.out.println("Network " + networkName + " not found.");
            return new ArrayList<>();
        }
    }

}
