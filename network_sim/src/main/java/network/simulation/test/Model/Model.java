package network.simulation.test.Model;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import network.simulation.test.Model.Nodes.CustomDevice;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.StandardDevice;
import network.simulation.test.UtilityClasses.ProjectLoader;
import network.simulation.test.UtilityClasses.ProjectSaver;

public class Model implements IModelView, IModelController {

    private static final String RESOURCES_DIR_DNS = "src/main/resources/dns/";


    String name;
    HashMap<String, Network> networks;
    HashMap<String, Device> devices;
    ArrayList<String> networkNames;
    transient ArrayList<Device> unassignedDevices;
    int devicesCreated;
    String path;
    String entryPoint;

    public Model() {
        this.name = "";
        this.networks = new HashMap<>();
        this.devices = new HashMap<>();
        this.networkNames = new ArrayList<>();
        this.unassignedDevices = new ArrayList<>();
        this.devicesCreated = 0;
        this.path = "./";
        this.entryPoint = null;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public HashMap<String, Network> getNetworks() {
        return this.networks;
    }
    public HashMap<String, Device> getDevices() {
        return this.devices;
    }
    public int getDevicesCreated() {
        return this.devicesCreated;
    }

    @Override
    public void createNetwork(String name, String adressRange) {
        Network network = new Network(name, adressRange);
        this.networks.put(name, network);
        this.networkNames.add(name);
    }

    @Override
    public void addStandardNetwork() {
        String number = String.valueOf(networks.size() + 1);
        String name = "network" + number;
        String adressRange = "192.168.100.0/24";
        Network network = new Network(name, adressRange);
        this.networks.put(name, network);
        this.networkNames.add(name);
    }

    @Override
    public void createDevice(String name, String baseImage) {
        String lowerCaseName = name.toLowerCase();
        Device device = new CustomDevice(lowerCaseName, baseImage);
        this.unassignedDevices.add(device);
        this.devices.put(lowerCaseName, device);
        this.devicesCreated++;
    }

    @Override
    public void addStandardDevice() {
        this.devicesCreated++;
        String number = String.valueOf(devicesCreated);
        String name = "device" + number;
        Device device = new StandardDevice(name);
        this.unassignedDevices.add(device);
        this.devices.put(name, device);
    }
    
    @Override
    public ArrayList<String> getNetworkNames() {
        return this.networkNames;
    }

    @Override
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
                    this.devices.remove(name);
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
                    this.devices.remove(name);
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
    
    @Override
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

    /**
     * Generates a Docker Compose file for the current model.
     * It includes all networks and their devices, along with their configurations.
     * The file is written to the specified path.
     * @param path the path where the docker-compose.yml file will be created
     */
    public void generateDockerCompose(Path path) {
        StringBuilder compose = new StringBuilder();
        compose.append("services:\n");

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
        for (Device device : devices.values()) {
            device.writeDockerfileToFile(Path.of(this.path));
        }
        generateDockerCompose(Paths.get(this.path + "/docker-compose.yml"));
    }
    
    public static String writeForwardZone(ArrayList<Device> devicesInNetwork, String domain, Path path) throws IOException {
        Path zonePath = path.resolve("zones/");
        StringBuilder zone = new StringBuilder();
        zone.append("$TTL 604800\n");
        zone.append("@ IN SOA ns1.").append(domain + ".local").append(". admin.").append(domain + ".local").append(". (\n");
        zone.append("    2     ; Serial\n");
        zone.append("    604800 ; Refresh\n");
        zone.append("    86400  ; Retry\n");
        zone.append("    2419200 ; Expire\n");
        zone.append("    604800 ) ; Negative Cache TTL\n\n");

        zone.append("@ IN NS ns1.").append(domain).append(".\n");

        for (Device d : devicesInNetwork) {
            if (d.getIpAddress() != null) {
                zone.append(d.getDNSLabel()).append(" IN A ").append(d.getIpAddress()).append("\n");
            }
        }
        String title = "db." + domain + ".local";
        Files.writeString(zonePath.resolve(title), zone.toString());

        return title;
    }

    public static String writeReverseZone(ArrayList<Device> devices, String domain, Path path, String addressRange) throws IOException {
        Path zonePath = path.resolve("zones/");
        StringBuilder zone = new StringBuilder();
        zone.append("$TTL 604800\n");
        zone.append("@ IN SOA ns1." + domain + ".local. admin." + domain + ".local. (\n");
        zone.append("    2     ; Serial\n");
        zone.append("    604800 ; Refresh\n");
        zone.append("    86400  ; Retry\n");
        zone.append("    2419200 ; Expire\n");
        zone.append("    604800 ) ; Negative Cache TTL\n\n");
    
        zone.append("@ IN NS ns1." + domain + ".local.\n");
    
        for (Device d : devices) {
            String ip = d.getIpAddress();
            if (ip != null) {
                String lastOctet = ip.substring(ip.lastIndexOf('.') + 1);
                zone.append(lastOctet).append(" IN PTR ").append(d.getDNSLabel()).append("." + domain + ".local.\n");
            }
        }
        String[] parts = addressRange.split("/");
        String[] octets = parts[0].split("\\.");

        // For /24, we reverse the first 3 octets
        String title = "db." + octets[2] + "." + octets[1] + "." + octets[0] + ".in-addr.arpa";
    
        Files.writeString(zonePath.resolve(title), zone.toString());
        return title;
    }
 
    public static void writeNamedConfLocal(String domain, Path path, String filenameOne, String filenameTWO) throws IOException {
        StringBuilder config = new StringBuilder();
        config.append("zone \"" + domain + ".local\" {\n");
        config.append("    type master;\n");
        config.append("    file \"/etc/bind/zones/" + filenameOne + "\";\n");
        config.append("};\n\n");
    
        config.append("zone \"" + filenameTWO.substring(3) + "\" {\n");
        config.append("    type master;\n");
        config.append("    file \"/etc/bind/zones/" + filenameTWO + "\";\n");
        config.append("};\n");
    
        Files.writeString(path.resolve("named.conf.local"), config.toString());
    }
    
    public void generateDNSFiles() throws IOException {
        for (Network network : networks.values()) {             
            Path path = Paths.get(RESOURCES_DIR_DNS + network.getName() + "/");
            Files.createDirectories(path);
            String domain = network.getName();
            ArrayList<Device> devicesInNetwork = network.getDevicesInNetwork();
            String filenameOne = writeForwardZone(devicesInNetwork, domain, path);
            String filenameTwo = writeReverseZone(devicesInNetwork, domain, path, network.getAdressRange());
            writeNamedConfLocal(domain, path, filenameOne, filenameTwo);
            
        }
    }

    @Override
    public ArrayList<String> getNetworkInfo(String name) {
        Network network = networks.get(name);
        return network.getDisplayInfo();
    }
    @Override
    public ArrayList<String> getDeviceInfo(String name) {
        Device device = devices.get(name);
        return device.getDisplayInfo();
    }
    @Override
    public void setPath(String path) {
        this.path = path;
    }
    @Override
    public String getPath() {
        return this.path;
    }
    @Override
    public ArrayList<Device> getAllDevices() {
        ArrayList<Device> allDevices = new ArrayList<>(unassignedDevices);
        for (Device device : devices.values()) {
            allDevices.add(device);
        }
        return allDevices;
    }
    @Override
    public String getEntryPoint() {
        return this.entryPoint;
    }

    @Override
    public void setEntryPoint(String name) {
        this.entryPoint = name;
    }
    @Override
    public void editDevice(String oldName, String newName, String newOS, String newServices, String entryPoint) {
        Device device = this.devices.get(oldName);
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
                this.setEntryPoint(device.getName());
            } else if (this.getEntryPoint().equals(device.getName())) {
                this.setEntryPoint(null);  
            }
        }
    }

    @Override
    public void loadModel(String path) {
        try {
            Path projectPath = Path.of(path);
            String json = Files.readString(projectPath.resolve("project.json"));
    
            // Deserialize full model (excluding unassignedDevices, which is transient)
            Model loadedModel = ProjectLoader.getCustomGson().fromJson(json, Model.class);
    
            // Manually restore unassignedDevices
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonElement devicesJson = jsonObject.get("unassignedDevices");
    
            if (devicesJson != null && !devicesJson.isJsonNull()) {
                Type deviceListType = new TypeToken<ArrayList<Device>>() {}.getType();
                ArrayList<Device> unassigned = ProjectLoader.getCustomGson().fromJson(devicesJson, deviceListType);
                this.unassignedDevices = unassigned;
            }
    
            // Copy remaining state
            this.name = loadedModel.getName();
            this.networks = loadedModel.getNetworks();
            this.devices = loadedModel.getDevices();
            this.networkNames = loadedModel.getNetworkNames();
            this.devicesCreated = loadedModel.getDevicesCreated();
            this.path = loadedModel.getPath();
            this.entryPoint = loadedModel.getEntryPoint();
    
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void saveModel() {
        try {
            ProjectSaver.saveProject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveModelAs(String name, String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveModelAs'");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((networks == null) ? 0 : networks.hashCode());
        result = prime * result + ((devices == null) ? 0 : devices.hashCode());
        result = prime * result + ((networkNames == null) ? 0 : networkNames.hashCode());
        result = prime * result + ((unassignedDevices == null) ? 0 : unassignedDevices.hashCode());
        result = prime * result + devicesCreated;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((entryPoint == null) ? 0 : entryPoint.hashCode());
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
        Model other = (Model) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (networks == null) {
            if (other.networks != null)
                return false;
        } else if (!networks.equals(other.networks))
            return false;
        if (devices == null) {
            if (other.devices != null)
                return false;
        } else if (!devices.equals(other.devices))
            return false;
        if (networkNames == null) {
            if (other.networkNames != null)
                return false;
        } else if (!networkNames.equals(other.networkNames))
            return false;
        if (unassignedDevices == null) {
            if (other.unassignedDevices != null)
                return false;
        } else if (!unassignedDevices.equals(other.unassignedDevices))
            return false;
        if (devicesCreated != other.devicesCreated)
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (entryPoint == null) {
            if (other.entryPoint != null)
                return false;
        } else if (!entryPoint.equals(other.entryPoint))
            return false;
        return true;
    }

    
}
