package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Network;

public class RouterDevice extends Device {
    transient ArrayList<Network> connectedNetworks;
    protected ArrayList<String> connectedNetworkNames;
    HashMap<String, String> IpAddresses;

    public RouterDevice(String name) {
        super(name);
        this.DNSLabel = name;
        this.connectedNetworks = new ArrayList<>();
        this.connectedNetworkNames = new ArrayList<>();
        this.IpAddresses = new HashMap<>();
        this.isEntryPoint = false;
        this.setBaseImage("ubuntu:latest");
        installPackagesFromFile("network_sim/src/main/resources/Text/RouterPackages.txt");
        installServicesFromFile("network_sim/src/main/resources/Text/RouterServices.txt");
    }

    public ArrayList<String> getIPAddresses() {
        ArrayList<String> ips = new ArrayList<>();
        for (String ip : IpAddresses.values()) {
            ips.add(ip);
        }
        return ips;
    }

    public void addNetworkTorouter(Network network) {
        if (!connectedNetworks.contains(network)) {
            connectedNetworks.add(network);
            network.addRouter(this);
        }
    }

    public ArrayList<Network> getConnectedNetworks() {
        return connectedNetworks;
    }

    public ArrayList<String> getConnectedNetworkNames() {
        return connectedNetworkNames;
    }

    public void setIPAddress(String name, String ip) {
        this.IpAddresses.put(name, ip);
    }

    /**
     * Reads a txt file containing a list of packages and installs them on the device.
     * @param filePath the path to the file containing the package list
     */
    public void installPackagesFromFile(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            String[] packages = content.split(",");
            for (String pkg : packages) {
                String trimmed = pkg.trim();
                if (!trimmed.isEmpty()) {
                    installPackage(trimmed);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read package list " + e.getMessage());
        }
    }

    /**
     * Reads a txt file containing a list of services and installs them on the device.
     * @param filePath the path to the file containing the service list
     */
    public void installServicesFromFile(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            String[] services = content.split(",");
            for (String srv : services) {
                String trimmed = srv.trim();
                if (!trimmed.isEmpty()) {
                    installService(trimmed);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read service list " + e.getMessage());
        }
    }

    @Override
    public void writeDockerfileToFile(Path filePath) {
        String dockerfileContent = generateDockerfile();
        Path entrypointPath = Paths.get("network_sim/src/main/resources/router/entrypoint.sh");
        try {
            Path deviceDir = filePath.resolve(this.name);
            Path dockerfilePath = deviceDir.resolve("Dockerfile");
            Path entrypointDestPath = deviceDir.resolve("entrypoint.sh");

            Files.createDirectories(deviceDir);

            // Write Dockerfile
            Files.writeString(dockerfilePath, dockerfileContent);

            // Copy entrypoint script
            Files.copy(entrypointPath, entrypointDestPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.err.println("Error writing Dockerfile or entrypoint: " + e.getMessage());
        }
    }

    @Override
    public String generateDockerfile() {
        StringBuilder dockerfile = new StringBuilder();
        dockerfile.append("FROM ").append(this.baseImage).append("\n\n");
        dockerfile.append("RUN apt-get update && apt-get install -y --no-install-recommends \\\n");

        for (int i = 0; i < getPackages().size(); i++) {
            dockerfile.append("    ").append(getPackages().get(i));
            if (i < getPackages().size() - 1) {
                dockerfile.append(" \\\n");
            } else {
                dockerfile.append(" \\\n    && rm -rf /var/lib/apt/lists/*\n\n");
            }
        }
    
        dockerfile.append("COPY entrypoint.sh /entrypoint.sh\n");
        dockerfile.append("RUN chmod +x /entrypoint.sh\n");
        dockerfile.append("\n");
        dockerfile.append("ENTRYPOINT [\"/entrypoint.sh\"]");

        return dockerfile.toString();
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }

    @Override
    public ArrayList<String> getDisplayInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("Device Name: " + getName());
        info.add("Base Image: " + this.baseImage);
        info.add("Running: " + isRunning());
        info.add("Entry Point: " + isEntryPoint());
        info.add("Installed Packages: " + String.join(", ", getPackages()));
        info.add("Installed Services: " + String.join(", ", getServices()));
        for (Network network : connectedNetworks) {
            info.add("Connected Network: " + network.getName() + " (" + network.getAddressRange() + ")");
        }
        for (String network : IpAddresses.keySet()) {
            info.add("IP Address: " + network + " -> " + IpAddresses.get(network));
        }
        return info;
    }
    
}
