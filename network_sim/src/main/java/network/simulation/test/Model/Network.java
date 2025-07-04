package network.simulation.test.Model;

import java.util.ArrayList;

import network.simulation.test.Model.Nodes.Device;

public class Network {

    private String name;
    private String adressRange;
    private int capacity;
    private ArrayList<Device> devicesInNetwork;

    public Network(String name, String adressRange) {
        this.name = name;
        this.adressRange = adressRange;
        this.capacity = 0;
        this.devicesInNetwork = new ArrayList<>();
        generateCapacity(adressRange);
    }
    
    public void addDevice(Device device) {
        if (this.capacity > 0) {
            this.devicesInNetwork.add(device);
            this.capacity--;
        }
        else {
            System.out.println("No more space in " + this.name);
        }
    } 
    
    public void removeDevice(Device device) {
        if (this.devicesInNetwork.remove(device)) {
            this.capacity++;
        } else {
            System.out.println("Device not found in " + this.name);
        }
    }
    
    private void generateCapacity(String range) {
        try {
            String[] parts = range.split("/");
            if(parts.length != 2) throw new IllegalArgumentException("Invalid address format.");

            int prefixLength = Integer.parseInt(parts[1]);
            int hostBits = 32 - prefixLength;

            int totalHosts = (int) Math.pow(2, hostBits);

            if (hostBits <= 1) {
                // /31 or /32: No broadcast/network distinction
                this.capacity = totalHosts;
            } else {
                this.capacity = totalHosts - 2;
            }
        } catch (Exception e) {
            System.err.println("Error parsing address: " + e.getMessage());
        }
    }

    public String getName() {
        return this.name;
    }

    public String getAdressRange() {
        return this.adressRange;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public ArrayList<Device> getDevicesInNetwork() {
        return this.devicesInNetwork;
    }

    
}
