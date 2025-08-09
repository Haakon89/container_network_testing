package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Nodes.CustomDevice;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.DeviceFactory;

public class DeviceManager {
    private Model model;

    public DeviceManager(Model model) {
        this.model = model;
    }

    public void createCustomDevice(String name, String baseImage) {
        String lowerCaseName = name.toLowerCase();
        Device device = new CustomDevice(lowerCaseName, baseImage);
        model.unassignedDevices.add(device);
        model.devices.put(lowerCaseName, device);
        model.deviceNames.add(name);
        model.devicesCreated++;
    }

    public void createDevice(String deviceType) {
        Device newDevice = DeviceFactory.buildDevice(deviceType);
        String name = newDevice.getName();
        model.unassignedDevices.add(newDevice);
        model.devices.put(name, newDevice);
        model.deviceNames.add(name);
        model.devicesCreated++;
    }

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

    public void assignDevice(String device, String network) {
        Device deviceToAssign = model.unassignedDevices.stream()
            .filter(d -> d.getName().equals(device))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + device));

        model.networks.get(network).addDevice(deviceToAssign);
        model.unassignedDevices.remove(deviceToAssign);
    }
    
    public String findDevice(String name) {
        ArrayList<Device> unassignedDevices = model.getUnassignedDevices();
        HashMap<String, Network> networks = model.getNetworks();
        for (Device device : unassignedDevices) {
            if (device.getName().equals(name)) {
                return "unassigned";
            }
        }
        for (Network network : networks.values()) {
            for (Device device : network.getDevicesInNetwork()) {
                if (device.getName().equals(name)) {
                    return network.getName();
                }
            }
        }
        return null; // Device not found
    }

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
