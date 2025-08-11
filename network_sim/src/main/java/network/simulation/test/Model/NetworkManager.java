package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.UtilityClasses.SubNetSplitter;

public class NetworkManager {
    Model model;

    public NetworkManager(Model model) {
        this.model = model;
    }

    /**
     * Creates a new network with the given name and address range.
     * @param name the name of the network
     * @param adressRange the address range of the network in CIDR notation
     */
    public void createNetwork(String name, String adressRange) {
        Network network = new Network(name, adressRange);
        model.networks.put(name, network);
        model.networkNames.add(name);
    }

    /**
     * Divides the usable address block into subnets for each network.
     * This ensures that each network has a unique address range.
     */
    private void divideNetworkAddresses() {
        ArrayList<String> addressBlocks = SubNetSplitter.splitNetwork(model.usableAddressBlock, model.networks.size());
        int counter = 0;
        for (Network network : model.networks.values()) {
            network.setAdressRange(addressBlocks.get(counter));
            counter++;
            ArrayList<Device> devicesToBeMoved = network.resetDeviceAddresses();
            if (!devicesToBeMoved.isEmpty()) {
                for (Device device : devicesToBeMoved) {
                    device.setIpAddress(null);
                    model.unassignedDevices.add(device);
                }
            }
        }
    }

    /**
     * Adds a standard network with a default address range.
     * The address range is divided into subnets for each network.
     */
    public void addStandardNetwork() {
        HashMap<String, Network> networks = model.getNetworks();
        String number = String.valueOf(networks.size() + 1);
        String name = "network" + number;
        String adressRange = null;
        Network network = new Network(name, adressRange);
        model.networks.put(name, network);
        model.networkNames.add(name);
        divideNetworkAddresses();
    }

    /**
     * Deletes a network by its name.
     * @param networkName the name of the network to be deleted
     */
    public void deleteNetwork(String networkName) {
        Network netToBeDeleted = model.networks.get(networkName);
        Iterator<Device> iterator = netToBeDeleted.getDevicesInNetwork().iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            String deviceName = device.getName();
            model.deviceNames.remove(deviceName);
            model.devicesCreated--;
            model.devices.remove(deviceName);
        }
        model.networks.remove(networkName);
        model.networkNames.remove(networkName);
        divideNetworkAddresses();
    }

    /**
     * Moves a device from one network to another or to unassigned devices.
     * @param destination the name of the destination network or "unassigned"
     * @param origin the name of the origin network or "unassigned"
     */
    public void moveDevices(String destination, String origin) {
        HashMap<String, Network> networks = model.getNetworks();
        Network destNet = networks.get(destination);
        if (origin.equals("unassigned")) {
            ArrayList<Device> unassignedDevices = model.getUnassignedDevices();
            Iterator<Device> iterator = unassignedDevices.iterator();
            while (iterator.hasNext()) {
                Device device = iterator.next();
                destNet.addDevice(device);
                iterator.remove();
            }
        }
        if (networks.containsKey(origin)) {
            Network origNet = networks.get(origin);
            Iterator<Device> iterator = origNet.getDevicesInNetwork().iterator();
            while (iterator.hasNext()) {
                Device device = iterator.next();
                destNet.addDevice(device);
                iterator.remove();
            }
        }
        
    }

    /**
     * Edits the properties of a network.
     * @param oldName the current name of the network
     * @param newName the new name for the network, can be null to keep the same name
     * @param newAddressRange the new address range for the network in CIDR notation, can be null to keep the same range
     */
    public void editNetwork(String oldName, String newName, String newAddressRange) {
        Network networkToEdit = model.networks.get(oldName);
        if (networkToEdit != null) {
            if (newName != null && !newName.isEmpty()) {
                networkToEdit.setName(newName);
            }
            if (newAddressRange != null && !newAddressRange.isEmpty()) {
                networkToEdit.setAdressRange(newAddressRange);
                ArrayList<Device> devicesToBeMoved = networkToEdit.resetDeviceAddresses();
                for (Device device : devicesToBeMoved) {
                    device.setIpAddress(null);
                    model.unassignedDevices.add(device);
                }
            }
        } else {
            System.out.println("Network " + oldName + " not found.");
        }
    }
}
