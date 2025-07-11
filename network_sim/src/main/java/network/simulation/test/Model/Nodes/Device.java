package network.simulation.test.Model.Nodes;

import java.nio.file.Path;
import java.util.ArrayList;

public abstract class Device {
    
    protected String name;
    protected ArrayList<String> services;
    private ArrayList<String> packages;
    protected boolean isRunning;
    protected String ipAddress; 

    public Device(String name) {
        this.name = name;
        this.ipAddress = null;
        this.services = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.isRunning = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * Takes in a service and adds it to the list of services the device should have
     */
    public void installService(String service) {
        if (!services.contains(service)) {
            services.add(service);
            System.out.println("Service " + service + " installed on device " + name);
        } else {
            System.out.println("Service " + service + " is already installed on device " + name);
        }
    }

    /**
     * Takes in a package and adds it to the list of packages the device should have
     */
    public void installPackage(String pkg) { 
        if (!packages.contains(pkg)) {
            packages.add(pkg);
            System.out.println("Package " + pkg + " installed on device " + name);
        } else {
            System.out.println("Package " + pkg + " is already installed on device " + name);
        }
    }

    public ArrayList<String> getPackages() {
        return packages;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    /**
     * Generates a Dockerfile for the device based on its services and packages.
     * @return the Dockerfile content as a String
     */
    public abstract String generateDockerfile();

    /**
     * Writes the Dockerfile content to a file at the specified path.
     * @param filepath the path where the Dockerfile should be written
     */
    public abstract void writeDockerfileToFile(Path filepath);
    public abstract void start();
    public abstract void stop();
}

    