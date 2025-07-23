package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StandardDevice extends Device{
    private Map<String, Integer> servicePorts;

    public StandardDevice(String name) {
        super(name);
        this.DNSLabel = name;
        this.setBaseImage("ubuntu:latest");
        installPackagesFromFile("network_sim/src/main/resources/Text/StandardPackages.txt");
        installServicesFromFile("network_sim/src/main/resources/Text/StandardServices.txt");
        this.servicePorts = new HashMap<>();
        servicePorts.put("ssh", 22);
        servicePorts.put("http", 80);
        servicePorts.put("https", 443);
        servicePorts.put("dns", 53);
        servicePorts.put("ftp", 21);
        servicePorts.put("ntp", 123);
        servicePorts.put("syslog", 514);
        servicePorts.put("cups", 631);
        servicePorts.put("avahi", 5353);         // mDNS/Bonjour
        servicePorts.put("cron", -1);            // No port (scheduled tasks)
        servicePorts.put("dbus", -1);            // Internal IPC, not over TCP
        servicePorts.put("snapd", -1);           // Usually UNIX sockets
        servicePorts.put("systemd-journald", -1);// Uses /dev/log or journald socket
        servicePorts.put("udev", -1);            // Device manager, no TCP
        servicePorts.put("network-manager", -1); // No public port (manages interfaces)

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
    public String generateDockerfile() {
        StringBuilder dockerfile = new StringBuilder();
        dockerfile.append("FROM ").append(this.baseImage).append("\n\n");
        dockerfile.append("RUN apt-get update && apt-get install -y \\\n");

        for (int i = 0; i < getPackages().size(); i++) {
            dockerfile.append("    ").append(getPackages().get(i));
            if (i < getPackages().size() - 1) {
                dockerfile.append(" \\\n");
            } else {
                dockerfile.append(" \\\n    && apt-get clean\n\n");
            }
        }
    
        dockerfile.append("WORKDIR /home/standard_user\n");
        ArrayList<String> services = getServices();
        for (String srv : services) {
            if (servicePorts.containsKey(srv)) {
                int port = servicePorts.get(srv);
                if (port > 0) {
                    dockerfile.append("EXPOSE ").append(port).append("\n");
                }
            }
        }
        dockerfile.append("\n");
        dockerfile.append("CMD [\"tail\", \"-f\", \"/dev/null\"]\n");

        return dockerfile.toString();
    }    

    @Override
    public void writeDockerfileToFile(Path filePath) {
        String dockerfileContent = generateDockerfile();

        try {
            Path deviceDir = filePath.resolve(this.name);
            Path dockerfilePath = deviceDir.resolve("Dockerfile");
            Files.createDirectories(deviceDir);
            Files.writeString(dockerfilePath, dockerfileContent);
        } catch (IOException e) {
            System.err.println("Error writing Dockerfile: " + e.getMessage());
        }
    }

    @Override
    public void start() {
        setRunning(true);
    }

    @Override
    public void stop() {
        setRunning(false);
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
        return info;
    }    
}
