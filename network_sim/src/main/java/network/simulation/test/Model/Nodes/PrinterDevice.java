package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrinterDevice extends Device {

    private Map<String, Integer> servicePorts;

    public PrinterDevice(String name) {
        super(name);
        this.DNSLabel = name;
        this.setBaseImage("debian:bookworm");

        // Printer-specific packages and services
        installPackagesFromFile("network_sim/src/main/resources/Text/PrinterPackages.txt");
        installServicesFromFile("network_sim/src/main/resources/Text/PrinterServices.txt");

        this.servicePorts = new HashMap<>();
        servicePorts.put("cups", 631);  
        /*       // IPP
        servicePorts.put("lpd", 515);          // LPD
        servicePorts.put("raw9100", 9100);     // JetDirect/Raw printing
        servicePorts.put("avahi", 5353);       // Bonjour/mDNS for discovery
        */
        
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
        /*
        dockerfile.append("RUN mkdir /var/run/sshd && \\").append("\n");
        dockerfile.append("    echo 'root:root' | chpasswd && \\").append("\n");
        dockerfile.append("    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config").append("\n");
        */
        dockerfile.append("RUN mkdir -p /var/run/cups /run/cups /etc/cups/ppd").append("\n\n");
        dockerfile.append(("COPY printer_config/entrypoint.sh /entrypoint.sh\n"));
        dockerfile.append(("COPY printer_config/cupsd.conf /etc/cups/cupsd.conf\n\n"));
        dockerfile.append("RUN chmod +x /entrypoint.sh\n\n");
        
        dockerfile.append("ENTRYPOINT [\"/usr/bin/tini\", \"--\"]").append("\n\n");

        dockerfile.append("WORKDIR /home/printer_user\n");
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
        /*
        dockerfile.append("CMD [\"bash\", \"-c\", ");
        dockerfile.append("\"mkdir -p /var/spool/fakeprinter && while true; do nc -lk -p 9100 > /var/spool/fakeprinter/job_$(date +%s).txt; done\"");
        dockerfile.append("]\n");
        */
        dockerfile.append("CMD [\"/entrypoint.sh\"]");
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
            Path src = Paths.get("network_sim/src/main/resources/printer");
            Path dest = deviceDir.resolve("printer_config");
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
