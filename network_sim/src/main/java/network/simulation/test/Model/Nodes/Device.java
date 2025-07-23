package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public abstract class Device {
    
    protected String name;
    protected ArrayList<String> services;
    private ArrayList<String> packages;
    protected boolean isRunning;
    protected boolean isEntryPoint;
    protected String ipAddress; 
    protected String baseImage;
    protected String DNSLabel;

    public Device(String name) {
        this.name = name;
        this.ipAddress = null;
        this.services = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.isRunning = false;
        this.isEntryPoint = false;
        this.baseImage = null;
        this.DNSLabel = null;
    }

    //constructor for json
    public Device() {}

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
    
    public void setBaseImage(String baseImage) {
        this.baseImage = baseImage;
    }

    public String getBaseImage() {
        return baseImage;
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

    public void setPackages(ArrayList<String> packages) {
        this.packages = packages;
    }

    public ArrayList<String> getPackages() {
        return packages;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
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

    public boolean isEntryPoint() {
        return isEntryPoint;
    }

    public void setEntryPoint(boolean isEntryPoint) {
        this.isEntryPoint = isEntryPoint;
    }

    public String getDNSLabel() {
        return this.DNSLabel;
    }

    /**
     * Writes the Dockerfile content to a file at the specified path.
     * @param filepath the path where the Dockerfile should be written
     */
    public abstract void writeDockerfileToFile(Path filePath);

    /**
     * Generates a Dockerfile for the device based on its services and packages.
     * @return the Dockerfile content as a String
     */
    public abstract String generateDockerfile();
    public abstract void start();
    public abstract void stop();

    public abstract ArrayList<String> getDisplayInfo();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((services == null) ? 0 : services.hashCode());
        result = prime * result + ((packages == null) ? 0 : packages.hashCode());
        result = prime * result + (isRunning ? 1231 : 1237);
        result = prime * result + (isEntryPoint ? 1231 : 1237);
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
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
        Device other = (Device) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (services == null) {
            if (other.services != null)
                return false;
        } else if (!services.equals(other.services))
            return false;
        if (packages == null) {
            if (other.packages != null)
                return false;
        } else if (!packages.equals(other.packages))
            return false;
        if (isRunning != other.isRunning)
            return false;
        if (isEntryPoint != other.isEntryPoint)
            return false;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        return true;
    }

    
}

    