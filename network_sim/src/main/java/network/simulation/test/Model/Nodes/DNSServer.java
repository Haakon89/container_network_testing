package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DNSServer extends Device {
    private final Map<String, Integer> servicePorts;

    public DNSServer(String name) {
        super(name);
        this.DNSLabel = "ns1";
        this.setBaseImage("ubuntu:latest");

        this.servicePorts = new HashMap<>();
        servicePorts.put("dns", 53);  // TCP and UDP

        installService("bind9");
        installPackage("bind9");
        installPackage("dnsutils"); // for tools like dig and nslookup
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

        dockerfile.append("WORKDIR /etc/bind\n");

        // Expose DNS port
        dockerfile.append("EXPOSE 53\n");
        dockerfile.append("EXPOSE 53/udp\n\n");

        // Start bind9 in foreground
        dockerfile.append("CMD [\"/usr/sbin/named\", \"-f\", \"-u\", \"bind\"]\n");

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
        info.add("Type: DNS Server");
        info.add("Base Image: " + getBaseImage());
        info.add("Running: " + isRunning());
        info.add("Entry Point: " + isEntryPoint());
        info.add("Installed Packages: " + String.join(", ", getPackages()));
        info.add("Installed Services: " + String.join(", ", getServices()));
        return info;
    }
    
}
