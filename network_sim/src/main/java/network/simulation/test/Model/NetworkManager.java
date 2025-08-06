package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import network.simulation.test.Model.Nodes.Device;

public class NetworkManager {
    Model model;

    public NetworkManager(Model model) {
        this.model = model;
    }

    public void createNetwork(String name, String adressRange) {
        Network network = new Network(name, adressRange);
        model.networks.put(name, network);
        model.networkNames.add(name);
    }

    public void addStandardNetwork() {
        HashMap<String, Network> networks = model.getNetworks();
        String number = String.valueOf(networks.size() + 1);
        String name = "network" + number;
        String adressRange = "192.168.100.0/24";
        Network network = new Network(name, adressRange);
        model.networks.put(name, network);
        model.networkNames.add(name);
    }

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
        model.networks.remove(netToBeDeleted.getName());
    }

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

    public void editNetwork(String oldName, String newName, String newAddressRange) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editNetwork'");
    }
}
