package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebServer extends Device {
    private Map<String, Integer> servicePorts;

    public WebServer(String name) {
        super(name);
        this.DNSLabel = "www";
        this.setBaseImage("ubuntu:latest");
        this.servicePorts = new HashMap<>();

        // Define key ports used for web servers
        servicePorts.put("http", 80);
        servicePorts.put("https", 443);

        // Install base services and packages for a web server
        installService("apache2");
        installService("openssl");

        installPackage("apache2");
        installPackage("openssl");
        installPackage("curl");
        installPackage("vim");
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

        dockerfile.append("WORKDIR /var/www/html\n");

        // Expose service ports
        for (String service : getServices()) {
            if (servicePorts.containsKey(service)) {
                int port = servicePorts.get(service);
                dockerfile.append("EXPOSE ").append(port).append("\n");
            }
        }

        dockerfile.append("\n");

        // Start Apache in the foreground
        dockerfile.append("CMD [\"apache2ctl\", \"-D\", \"FOREGROUND\"]\n");

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
        info.add("Type: Web Server");
        info.add("Base Image: " + this.baseImage);
        info.add("Running: " + isRunning());
        info.add("Entry Point: " + isEntryPoint());
        info.add("Installed Packages: " + String.join(", ", getPackages()));
        info.add("Installed Services: " + String.join(", ", getServices()));
        return info;
    }

    
}
