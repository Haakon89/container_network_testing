package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.List;

import network.simulation.test.Model.Nodes.DNSServer;
import network.simulation.test.Model.Nodes.Device;

public class Network {

    private String name;
    private String adressRange;
    private int capacity;
    private String gatewayAddress;
    private ArrayList<Device> devicesInNetwork;
    private ArrayList<String> reusableIPAddresses;

    private int nextHostOffset;

    public Network(String name, String adressRange) {
        this.name = name;
        this.adressRange = adressRange;
        this.capacity = 0;
        this.devicesInNetwork = new ArrayList<>();
        this.reusableIPAddresses = new ArrayList<>();
        this.nextHostOffset = 1;
        //generateCapacity(adressRange);
    }

    // Constructor for JSON deserialization
    public Network() {}
    
    /**
     * Adds a device to the network if there is capacity.
     * @param device the device to be added
     */
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
    

    /**
     * Removes a device from the network and makes its IP address reusable.
     * @param device the device to be removed
     */
    public void removeDevice(Device device) {
        if (this.devicesInNetwork.remove(device)) {
            this.capacity++;
            this.reusableIPAddresses.add(device.getIpAddress());
        } else {
            System.out.println("Device not found in " + this.name);
        }
    }
    
    /**
     * Generates the capacity of the network based on the address range.
     * The capacity is calculated based on the prefix length of the CIDR notation.
     * @param range the address range in CIDR notation
     */
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

    public void resetAllocatorFor(String cidr, boolean reserveGateway) {
        this.adressRange = cidr; // (typo?) addressRange
        // Reserve .1 (or .129 etc.) for the gateway deterministically
        nextHostOffset = reserveGateway ? 1 : 0;
    }

    public String computeGateway(String cidr) {
        String[] parts = cidr.split("/");
        int base = ipToInt(parts[0]);
        int firstUsable = base + 1;
        return intToIp(firstUsable);
    }

    public String generateIPAddress() {
        if (!reusableIPAddresses.isEmpty()) {
            return reusableIPAddresses.remove(0);
        }

        String[] parts = this.adressRange.split("/");
        int base = ipToInt(parts[0]);
        int prefix = Integer.parseInt(parts[1]);
        int hostBits = 32 - prefix;
        int blockSize = 1 << hostBits;

        int firstUsable = base + 1;
        int lastUsable  = base + blockSize - 2;

        int candidate = firstUsable + nextHostOffset;
        if (candidate > lastUsable) {
            throw new IllegalStateException("No more IPs in " + this.adressRange);
        }
        nextHostOffset++;
        return intToIp(candidate);
    }

    private static int ipToInt(String ip) {
        String[] o = ip.split("\\.");
        return (Integer.parseInt(o[0]) << 24)
            | (Integer.parseInt(o[1]) << 16)
            | (Integer.parseInt(o[2]) << 8)
            |  Integer.parseInt(o[3]);
    }

    private static String intToIp(int v) {
        return ((v >>> 24) & 0xFF) + "." + ((v >>> 16) & 0xFF) + "." +
            ((v >>> 8) & 0xFF) + "." + (v & 0xFF);
    }
    

    private boolean checkCapacity() {
        if (this.capacity <= 0) {
            return false;
        }
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAdressRange(String adressRange) {
        this.adressRange = adressRange;
        generateCapacity(adressRange);
    }

    public String getAdressRange() {
        return this.adressRange;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setDevicesInNetwork(ArrayList<Device> devices) {
        this.devicesInNetwork = devices;
    }
    public ArrayList<Device> getDevicesInNetwork() {
        return this.devicesInNetwork;
    }

    public void setReusableIPAddresses(ArrayList<String> reusableIPAddresses) {
        this.reusableIPAddresses = reusableIPAddresses;
    }
    
    public ArrayList<String> getReusableIPAddresses() {
        return this.reusableIPAddresses;
    }

    public void setGateway(String gatewayAddress) {
        this.gatewayAddress = gatewayAddress;
    }

    public String getGateway() {
        return this.gatewayAddress;
    }
    /**
     * resets the IP addresses of all devices in the network.
     * It removes all devices from the network, clears the reusable IP addresses,
     * and then adds the devices back to the network with new IP addresses.
     * used when the address range of the network is changed or when the network is reset.
     */
    public ArrayList<Device> resetDeviceAddresses() {
        generateCapacity(adressRange);
        ArrayList<Device> oldDevices = new ArrayList<>(this.devicesInNetwork);
        ArrayList<Device> devicesToBeMoved = new ArrayList<>();
        for (Device device : oldDevices) {
            this.removeDevice(device);
        }
        this.reusableIPAddresses.clear();
        this.gatewayAddress = computeGateway(adressRange);
        if (!oldDevices.isEmpty()) {
            for (Device device : oldDevices) {
                if (checkCapacity()) {
                    this.addDevice(device);
                } else {
                    System.out.println("No more space in " + this.name + " for device " + device.getName());
                    devicesToBeMoved.add(device);
                }
            }
        }
        return devicesToBeMoved;
    }
    /**
     * Generates a Docker Compose configuration for the network.
     * It includes all devices in the network with their configurations.
     * @return a string representation of the Docker Compose configuration
     */
    public String getComposeInfo() {
        Device dns = hasDNS();
        StringBuilder sb = new StringBuilder();
        for (Device device : this.devicesInNetwork) {
            sb.append("  ").append(device.getName()).append(":\n");
            sb.append("    build: ./" + device.getName() + "\n");
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
            sb.append("        ipv4_address: ").append(device.getIpAddress()).append("\n");
            if (dns != null && device != dns) {
                sb.append("    dns:\n");
                sb.append("      - " + dns.getIpAddress()).append("\n\n");
            }
            else {
                sb.append("\n");
            }
            
        }

        return sb.toString();
    }

    public Device hasDNS() {
        for (Device device : this.devicesInNetwork) {
            if (device instanceof DNSServer) {
                return device;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((adressRange == null) ? 0 : adressRange.hashCode());
        result = prime * result + capacity;
        result = prime * result + ((gatewayAddress == null) ? 0 : gatewayAddress.hashCode());
        result = prime * result + ((devicesInNetwork == null) ? 0 : devicesInNetwork.hashCode());
        result = prime * result + ((reusableIPAddresses == null) ? 0 : reusableIPAddresses.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Network other = (Network) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (adressRange == null) {
            if (other.adressRange != null)
                return false;
        } else if (!adressRange.equals(other.adressRange))
            return false;
        if (capacity != other.capacity)
            return false;
        if (gatewayAddress == null) {
            if (other.gatewayAddress != null)
                return false;
        } else if (!gatewayAddress.equals(other.gatewayAddress))
            return false;
        if (devicesInNetwork == null) {
            if (other.devicesInNetwork != null)
                return false;
        } else if (!devicesInNetwork.equals(other.devicesInNetwork))
            return false;
        if (reusableIPAddresses == null) {
            if (other.reusableIPAddresses != null)
                return false;
        } else if (!reusableIPAddresses.equals(other.reusableIPAddresses))
            return false;
        return true;
    }

    public ArrayList<String> getDisplayInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("Network Name: " + this.name);
        info.add("Address Range: " + this.adressRange);
        info.add("Capacity: " + this.capacity);
        info.add("Gateway Address: " + this.gatewayAddress);
        info.add("Devices in Network:");
        for (Device device : this.devicesInNetwork) {
            info.add("  - " + device.getName() + " (" + device.getIpAddress() + ")");
        }
        return info;
    }
    
    
}
