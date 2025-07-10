package network.simulation.test.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Nodes.CustomDevice;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.StandardDevice;

public class Model implements IModelView, IModelController {

    String name;
    HashMap<String, Network> networks;
    ArrayList<String> networkNames;
    ArrayList<Device> unassignedDevices;
    int devicesCreated;

    public Model() {
        this.name = "";
        this.networks = new HashMap<>();
        this.networkNames = new ArrayList<>();
        this.unassignedDevices = new ArrayList<>();
        devicesCreated = 0;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public void createNetwork(String name, String adressRange) {
        Network network = new Network(name, adressRange);
        this.networks.put(name, network);
        this.networkNames.add(name);
        System.out.println("Network " + name + " created with address range " + adressRange);
        System.out.println("Available networks: " + networks.keySet());
    }

    @Override
    public void addStandardNetwork() {
        String number = String.valueOf(networks.size() + 1);
        String name = "Network" + number;
        String adressRange = "192.168.100.0/24";
        Network network = new Network(name, adressRange);
        this.networks.put(name, network);
        this.networkNames.add(name);
        System.out.println("Network " + name + " created with address range " + adressRange);
        System.out.println("Available networks: " + networks.keySet());
    }

    @Override
    public void createDevice(String name, String baseImage) {
        Device device = new CustomDevice(name, baseImage);
        this.unassignedDevices.add(device);
        this.devicesCreated++;
    }

    @Override
    public void addStandardDevice() {
        this.devicesCreated++;
        String number = String.valueOf(devicesCreated);
        String name = "Device" + number;
        Device device = new StandardDevice(name);
        this.unassignedDevices.add(device);
    }
    
    @Override
    public ArrayList<String> getNetworkNames() {
        return this.networkNames;
    }

    public ArrayList<Device> getDevicesInNetwork(String networkName) {
        Network network = networks.get(networkName);
        if (network != null) {
            return network.getDevicesInNetwork();
        } else {
            System.out.println("Network " + networkName + " not found.");
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<Device> getUnassignedDevices() {
        return this.unassignedDevices;
    }

    @Override
    public void deleteDevice(String name, String home) {
        if (home.equals("unassigned")) {
            for (Device device : unassignedDevices) {
                if (device.getName().equals(name)) {
                    unassignedDevices.remove(device);
                    return;
                }
            }
        } else {
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
                } else {
                    System.out.println("device " + name + " not found in network " + home + ".");
                }
            } else {
                System.out.println("Network " + home + " not found.");
            }
        }
    }

    @Override
    public void assignDevice(String device, String network) {
        Device deviceToAssign = this.unassignedDevices.stream()
            .filter(d -> d.getName().equals(device))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + device));

        this.networks.get(network).addDevice(deviceToAssign);
        this.unassignedDevices.remove(deviceToAssign);
    }

    public void writeDockerfile(String path) {
        // Implementation for writing Dockerfile
    }
    
    public String findDevice(String name) {
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

    public void generateDockerCompose(Path path) {
        StringBuilder compose = new StringBuilder();
        compose.append("version '3.9':\n\nservices:\n");

        for (Network network : networks.values()) {
            compose.append(network.getComposeInfo());
        }

        compose.append("networks:\n");

        for (Network network : networks.values()) {
            compose.append("  ").append(network.getName()).append(":\n");
            compose.append("    driver: bridge\n");
            compose.append("    ipam:\n");
            compose.append("      config:\n");
            compose.append("        - subnet: ").append(network.getAdressRange()).append("\n");
            compose.append("          gateway: ").append(network.getGateway()).append("\n");
        }

        try {
            Files.writeString(path, compose.toString());
        } catch (IOException e) {
            System.err.println("Error writing docker-compose.yml: " + e.getMessage());
        }
    }
    @Override
    public void buildProject() {
        generateDockerCompose(Paths.get("network_sim/src/main/resources/Docker/docker-compose.yml"));
    }

}
