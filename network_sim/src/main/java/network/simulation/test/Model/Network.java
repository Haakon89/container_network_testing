package network.simulation.test.Model;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import network.simulation.test.Model.Nodes.Device;

public class Network {

    private String name;
    private String adressRange;
    private int capacity;
    private String gatewayAddress;
    private ArrayList<Device> devicesInNetwork;
    private ArrayList<String> reusableIPAddresses;

    public Network(String name, String adressRange) {
        this.name = name;
        this.adressRange = adressRange;
        this.capacity = 0;
        this.devicesInNetwork = new ArrayList<>();
        this.reusableIPAddresses = new ArrayList<>();
        generateCapacity(adressRange);
        this.gatewayAddress = generateIPAddress();
    }
    
    public void addDevice(Device device) {
        if (checkCapacity()) {
            this.devicesInNetwork.add(device);
            device.setIpAddress(generateIPAddress());
            System.out.println(device.getIpAddress());
            this.capacity--;
        }
        else {
            System.out.println("No more space in " + this.name);
        }
    } 
    
    public void removeDevice(Device device) {
        if (this.devicesInNetwork.remove(device)) {
            this.capacity++;
            this.reusableIPAddresses.add(device.getIpAddress());
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

    private String generateIPAddress() {
        if (this.reusableIPAddresses.size() > 0) {
            return this.reusableIPAddresses.remove(0);
        }
        int deviceNumber = this.devicesInNetwork.size() + 1;
        String range = this.adressRange.split("/")[0];
        String ipAddress = range.substring(0, range.length() - 1) + String.valueOf(deviceNumber);
        return ipAddress;
    }

    private boolean checkCapacity() {
        if (this.capacity <= 0) {
            return false;
        }
        return true;
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

    public String getGateway() {
        return this.gatewayAddress;
    }

    public String getComposeInfo() {
        StringBuilder sb = new StringBuilder();
        for (Device device : this.devicesInNetwork) {
            device.writeDockerfileToFile(Paths.get("network_sim/src/main/resources/Docker/"));
            sb.append("  ").append(device.getName()).append(":\n");
            sb.append("    container_name: ").append(device.getName()).append("\n");
            /*
            if (device.getStartupCommand() != null && !device.getStartupCommand().isBlank()) {
                sb.append("    command: ").append(device.getStartupCommand()).append("\n");
            }

            List<Integer> ports = device.getExposedPorts();
            if (ports != null && !ports.isEmpty()) {
                sb.append("    ports:\n");
                for (int port : ports) {
                    sb.append("      - \"").append(port).append(":").append(port).append("\"\n");
                }
            }
            */
            sb.append("    networks:\n");
            sb.append("      ").append(this.name).append(":\n");
            sb.append("        ipv4_address: ").append(device.getIpAddress()).append("\n\n");
        }

        return sb.toString();
    }
    
    
}
