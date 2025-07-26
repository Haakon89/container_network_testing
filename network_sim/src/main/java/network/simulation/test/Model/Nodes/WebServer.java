package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebServer extends Device {
    private Map<String, Integer> servicePorts;

    public WebServer(String name) {
        super(name);
        this.DNSLabel = "www";
        this.setBaseImage("httpd:2.4");
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
        
        dockerfile.append("FROM httpd:2.4\n\n");

        dockerfile.append("# Copy website content into the default web root\n");
        dockerfile.append("COPY web/ /usr/local/apache2/htdocs/\n\n");

        dockerfile.append("# Expose HTTP port\n");
        dockerfile.append("EXPOSE 80\n\n");

        dockerfile.append("# Start Apache in the foreground\n");
        dockerfile.append("CMD [\"httpd-foreground\"]\n");

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
            Path src = Paths.get("network_sim/src/main/resources/web");
            Path dest = deviceDir.resolve("web");
            Files.createDirectories(dest);
            Files.walk(src).forEach(source -> {
                try {
                    Path target = dest.resolve(src.relativize(source));
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(target);
                    } else {
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    }
            } catch (IOException e) {
                System.err.println("Error copying file: " + e.getMessage());
            }
            });

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
