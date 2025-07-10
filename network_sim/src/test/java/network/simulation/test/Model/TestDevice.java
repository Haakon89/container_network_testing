package network.simulation.test.Model;
import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Nodes.CustomDevice;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.StandardDevice;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDevice {
    private String nodeNameOne = "Node1";
    private String nodeNameTwo = "Node2";
    private String baseImage = "ubuntu:latest";
    private String ipAddress = "192.168.100.2";
    private Device nodeOne;
    private Device nodeTwo;
    
    @BeforeEach
    void setup() {
        nodeOne = new CustomDevice(nodeNameOne, baseImage);
        nodeTwo = new StandardDevice(nodeNameTwo);
    }

    @Test
    public void testSetGetName() {
        assertEquals(nodeNameOne, nodeOne.getName(), "Node name should match the set value.");
        assertEquals(nodeNameTwo, nodeTwo.getName(), "Node name should match the set value.");
        String newName = "newName";
        nodeOne.setName(newName);
        assertEquals(newName, nodeOne.getName(), "Node name should be updated to the new value.");
    }

    @Test
    public void testSetGetIpAddress() {
        nodeOne.setIpAddress(ipAddress);
        assertEquals(ipAddress, nodeOne.getIpAddress(), "IP address should match the set value.");
        nodeTwo.setIpAddress(ipAddress);
        assertEquals(ipAddress, nodeTwo.getIpAddress(), "IP address should match the set value.");
    }

    @Test
    public void testInstallService() {
        String serviceName = "ssh";
        nodeOne.installService(serviceName);
        assertTrue(nodeOne.getServices().contains(serviceName), "Service should be installed and present in the service list.");
    }

    @Test
    public void testInstallPackage() {
        String packageName = "curl";
        nodeOne.installPackage(packageName);
        assertTrue(nodeOne.getPackages().contains(packageName), "Package should be installed and present in the package list.");
    }
    
}