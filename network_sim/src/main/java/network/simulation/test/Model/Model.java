package network.simulation.test.Model;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import network.simulation.test.Model.Nodes.CustomDevice;
import network.simulation.test.Model.Nodes.DNSServer;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.DeviceFactory;
import network.simulation.test.UtilityClasses.FileWriter;
import network.simulation.test.UtilityClasses.ProjectLoader;
import network.simulation.test.UtilityClasses.ProjectSaver;

public class Model implements IModelView, IModelController {

    protected String name;
    protected HashMap<String, Network> networks;
    protected HashMap<String, Device> devices;
    protected ArrayList<String> networkNames;
    protected ArrayList<String> deviceNames;
    transient ArrayList<Device> unassignedDevices;
    protected int devicesCreated;
    protected String path;
    protected String entryPoint;
    private transient DeviceManager deviceManager;
    private transient NetworkManager networkManager;

    public Model() {
        this.name = "";
        this.networks = new HashMap<>();
        this.devices = new HashMap<>();
        this.networkNames = new ArrayList<>();
        this.deviceNames = new ArrayList<>();
        this.unassignedDevices = new ArrayList<>();
        this.devicesCreated = 0;
        this.path = "./";
        this.entryPoint = null;
        this.deviceManager = new DeviceManager(this);
        this.networkManager = new NetworkManager(this);
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
    public ArrayList<String> getNetworkNames() {
        return this.networkNames;
    }

    @Override
    public ArrayList<String> getDeviceNames() {
        return this.deviceNames;
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
    public void createNetwork(String name, String adressRange) {
        this.networkManager.createNetwork(name, adressRange);
    }

    @Override
    public void addStandardNetwork() {
        this.networkManager.addStandardNetwork();
    }

    @Override
    public void deleteNetwork(String networkName) {
        this.networkManager.deleteNetwork(networkName);
    }

    @Override
    public void moveDevices(String destination, String origin) {
        this.networkManager.moveDevices(destination, origin);
    }

    @Override
    public void editNetwork(String oldName, String newName, String newAddressRange) {
        this.networkManager.editNetwork(oldName, newName, newAddressRange);
    }

    //Device related Methods methods can be found in the DeviceMangaer Class
    @Override
    public void createCustomDevice(String name, String baseImage) {
        this.deviceManager.createCustomDevice(name, baseImage);
    }

    @Override
    public void createDevice(String deviceType) {
        this.deviceManager.createDevice(deviceType);
    }
    
    
    @Override
    public ArrayList<Device> getDevicesInNetwork(String networkName) {
        return this.deviceManager.getDevicesInNetwork(networkName);
    }

    @Override
    public ArrayList<Device> getUnassignedDevices() {
        return this.unassignedDevices;
    }

    @Override
    public void deleteDevice(String name, String home) {
        this.deviceManager.deleteDevice(name, home);
    }

    @Override
    public void assignDevice(String device, String network) {
        this.deviceManager.assignDevice(device, network);

    }
    
    @Override
    public String findDevice(String name) {
        return this.deviceManager.findDevice(name);
    }

    @Override
    public void editDevice(String oldName, String newName, String newOS, String newServices, String entryPoint) {
        this.deviceManager.editDevice(oldName, newName, newOS, newServices, entryPoint);
    }

    @Override
    public void buildProject() {
        for (Device device : devices.values()) {          
            device.writeDockerfileToFile(Path.of(this.path));
            if (device instanceof DNSServer) {
                try {
                    generateDNSFiles();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        FileWriter.generateDockerCompose(Paths.get(this.path + "/docker-compose.yml"), this.networks);
    }
    
    /**
     * generates the neccesary DNS files to handle the services on the local network
     * @throws IOException
     */
    private void generateDNSFiles() throws IOException {
        for (Network network : networks.values()) {             
            Path path = Paths.get(this.path + "/dns1/");
            Files.createDirectories(path);
            Path zonePath = Paths.get(path + "/zones/");
            Files.createDirectories(zonePath);
            String domain = network.getName();
            ArrayList<Device> devicesInNetwork = network.getDevicesInNetwork();
            String filenameOne = FileWriter.writeForwardZone(devicesInNetwork, domain, path);
            String filenameTwo = FileWriter.writeReverseZone(devicesInNetwork, domain, path, network.getAdressRange());
            FileWriter.writeNamedConfLocal(domain, path, filenameOne, filenameTwo);
            
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

    public void resetdeviceFactory() {
        DeviceFactory.resetCounters();
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
