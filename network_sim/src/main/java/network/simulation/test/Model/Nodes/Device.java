package network.simulation.test.Model.Nodes;

import java.util.ArrayList;
import java.util.HashMap;

public class Device {
    
    private String name;
    private String baseImage;
    private ArrayList<String> packagesToInstall;
    private ArrayList<String> portsToExpose;
    private ArrayList<String> startupCommands;
    private HashMap<String, String> envVars;
    private HashMap<String, String> volumes;
    private ArrayList<String> networks;
    private boolean priveledge;
    private ArrayList<String> capabilities;

    public Device(String name, String baseImage) {
        this.name = name;
        this.baseImage = baseImage;
        this.packagesToInstall = new ArrayList<>();
        this.portsToExpose = new ArrayList<>();
        this.startupCommands = new ArrayList<>();
        this.envVars = new HashMap<>();
        this.volumes = new HashMap<>();
        this.networks = new ArrayList<>();
        this.priveledge = false;
        this.capabilities = new ArrayList<>();

    }

    public ArrayList<String> getPackagesToInstall() {
        return packagesToInstall;
    }

    public void setPackagesToInstall(ArrayList<String> packagesToInstall) {
        this.packagesToInstall = packagesToInstall;
    }

    public ArrayList<String> getPortsToExpose() {
        return portsToExpose;
    }

    public void setPortsToExpose(ArrayList<String> portsToExpose) {
        this.portsToExpose = portsToExpose;
    }

    public ArrayList<String> getStartupCommands() {
        return startupCommands;
    }

    public void setStartupCommands(ArrayList<String> startupCommands) {
        this.startupCommands = startupCommands;
    }

    public HashMap<String, String> getEnvVars() {
        return envVars;
    }

    public void setEnvVars(HashMap<String, String> envVars) {
        this.envVars = envVars;
    }

    public HashMap<String, String> getVolumes() {
        return volumes;
    }

    public void setVolumes(HashMap<String, String> volumes) {
        this.volumes = volumes;
    }

    public ArrayList<String> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<String> networks) {
        this.networks = networks;
    }

    public boolean isPriveledge() {
        return priveledge;
    }

    public void setPriveledge(boolean priveledge) {
        this.priveledge = priveledge;
    }

    public ArrayList<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(ArrayList<String> capabilities) {
        this.capabilities = capabilities;
    }

    public String getName() {
        return name;
    }

    public String getBaseImage() {
        return baseImage;
    }

    public void addCapability(String string) {
        this.capabilities.add(string);
    }

    public void addNetwork(String string) {
        this.networks.add(string);
    }

    public void addVolume(String string, String string2) {
        this.volumes.put(string, string2);
    }

    public void addEnvVar(String string, String string2) {
        if (string != null && string2 != null) {
            this.envVars.put(string, string2);
        } else {
            throw new IllegalArgumentException("Environment variable key and value cannot be null");
        }
    }

    public void addStartupCommand(String string) {
        if (string != null && !string.isEmpty()) {
            this.startupCommands.add(string);
        } else {
            throw new IllegalArgumentException("Startup command cannot be null or empty");
        }
    }

    public void addPort(String string) {
        if (string != null && !string.isEmpty()) {
            this.portsToExpose.add(string);
        } else {
            throw new IllegalArgumentException("Port cannot be null or empty");
        }
    }

    public void addPackage(String string) {
        if (string != null && !string.isEmpty()) {
            this.packagesToInstall.add(string);
        } else {
            throw new IllegalArgumentException("Package name cannot be null or empty");
        }
    }

    public void removeNetwork(String networkName) {
        if (networkName != null && !networkName.isEmpty()) {
            this.networks.remove(networkName);
        } else {
            throw new IllegalArgumentException("Network name cannot be null or empty");
        }
    }

    public void removeVolume(String hostPath) {
        if (hostPath != null && !hostPath.isEmpty()) {
            this.volumes.remove(hostPath);
        } else {
            throw new IllegalArgumentException("Host path cannot be null or empty");
        }
    }

    public void removeEnvVar(String key) {
        if (key != null && !key.isEmpty()) {
            this.envVars.remove(key);
        } else {
            throw new IllegalArgumentException("Environment variable key cannot be null or empty");
        }
    }

    public void removeStartupCommand(String command) {
        if (command != null && !command.isEmpty()) {
            this.startupCommands.remove(command);
        } else {
            throw new IllegalArgumentException("Startup command cannot be null or empty");
        }
    }

    public void removePort(String port) {
        if (port != null && !port.isEmpty()) {
            this.portsToExpose.remove(port);
        } else {
            throw new IllegalArgumentException("Port cannot be null or empty");
        }
    }

    public void removePackage(String packageName) {
        if (packageName != null && !packageName.isEmpty()) {
            this.packagesToInstall.remove(packageName);
        } else {
            throw new IllegalArgumentException("Package name cannot be null or empty");
        }
    }

    public void removeCapability(String capability) {
        if (capability != null && !capability.isEmpty()) {
            this.capabilities.remove(capability);
        } else {
            throw new IllegalArgumentException("Capability cannot be null or empty");
        }
    }
    
}
