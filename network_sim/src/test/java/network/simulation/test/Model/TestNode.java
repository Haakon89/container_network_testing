package network.simulation.test.Model;
import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Nodes.Device;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNode {
    private String nodeName = "Node1";
    private String baseImage = "ubuntu:latest";
    private Device nodeOne;
    
    @BeforeEach
    void setup() {
        nodeOne = new Device(nodeName, baseImage);
    }
    
    @Test
    void testNodeInitialization() {
        assertEquals(nodeName, nodeOne.getName());
        assertEquals(baseImage, nodeOne.getBaseImage());
        assertEquals(0, nodeOne.getPackagesToInstall().size());
        assertEquals(0, nodeOne.getPortsToExpose().size());
        assertEquals(0, nodeOne.getStartupCommands().size());
        assertEquals(0, nodeOne.getEnvVars().size());
        assertEquals(0, nodeOne.getVolumes().size());
        assertEquals(0, nodeOne.getNetworks().size());
        assertEquals(false, nodeOne.isPriveledge());
        assertEquals(0, nodeOne.getCapabilities().size());
    }

    @Test
    void testAddPackage() {
        String packageName = "curl";
        nodeOne.addPackage(packageName);
        assertEquals(1, nodeOne.getPackagesToInstall().size());
        assertEquals(packageName, nodeOne.getPackagesToInstall().get(0));
    }

    @Test
    void testAddPort() {
        String port = "8080";
        nodeOne.addPort(port);
        assertEquals(1, nodeOne.getPortsToExpose().size());
        assertEquals(port, nodeOne.getPortsToExpose().get(0));
    }

    @Test
    void testAddStartupCommand() {
        String command = "echo Hello World";
        nodeOne.addStartupCommand(command);
        assertEquals(1, nodeOne.getStartupCommands().size());
        assertEquals(command, nodeOne.getStartupCommands().get(0));
    }

    @Test
    void testAddEnvVar() {
        String key = "ENV_VAR";
        String value = "value";
        nodeOne.addEnvVar(key, value);
        assertEquals(1, nodeOne.getEnvVars().size());
        assertEquals(value, nodeOne.getEnvVars().get(key));
    }

    @Test
    void testAddVolume() {
        String hostPath = "/host/path";
        String containerPath = "/container/path";
        nodeOne.addVolume(hostPath, containerPath);
        assertEquals(1, nodeOne.getVolumes().size());
        assertEquals(containerPath, nodeOne.getVolumes().get(hostPath));
    }

    @Test
    void testAddNetwork() {
        String networkName = "testNetwork";
        nodeOne.addNetwork(networkName);
        assertEquals(1, nodeOne.getNetworks().size());
        assertEquals(networkName, nodeOne.getNetworks().get(0));
    }

    @Test
    void testSetPriveledge() {
        nodeOne.setPriveledge(true);
        assertEquals(true, nodeOne.isPriveledge());
    }

    @Test
    void testAddCapability() {
        String capability = "CAP_NET_ADMIN";
        nodeOne.addCapability(capability);
        assertEquals(1, nodeOne.getCapabilities().size());
        assertEquals(capability, nodeOne.getCapabilities().get(0));
    }

    @Test
    void testRemoveCapability() {
        String capability = "CAP_NET_ADMIN";
        nodeOne.addCapability(capability);
        assertEquals(1, nodeOne.getCapabilities().size());
        nodeOne.removeCapability(capability);
        assertEquals(0, nodeOne.getCapabilities().size());
    }

    @Test
    void testRemovePackage() {
        String packageName = "curl";
        nodeOne.addPackage(packageName);
        assertEquals(1, nodeOne.getPackagesToInstall().size());
        nodeOne.removePackage(packageName);
        assertEquals(0, nodeOne.getPackagesToInstall().size());
    }

    @Test
    void testRemovePort() {
        String port = "8080";
        nodeOne.addPort(port);
        assertEquals(1, nodeOne.getPortsToExpose().size());
        nodeOne.removePort(port);
        assertEquals(0, nodeOne.getPortsToExpose().size());
    }

    @Test
    void testRemoveStartupCommand() {
        String command = "echo Hello World";
        nodeOne.addStartupCommand(command);
        assertEquals(1, nodeOne.getStartupCommands().size());
        nodeOne.removeStartupCommand(command);
        assertEquals(0, nodeOne.getStartupCommands().size());
    }

    @Test
    void testRemoveEnvVar() {
        String key = "ENV_VAR";
        String value = "value";
        nodeOne.addEnvVar(key, value);
        assertEquals(1, nodeOne.getEnvVars().size());
        nodeOne.removeEnvVar(key);
        assertEquals(0, nodeOne.getEnvVars().size());
    }

    @Test
    void testRemoveVolume() {
        String hostPath = "/host/path";
        String containerPath = "/container/path";
        nodeOne.addVolume(hostPath, containerPath);
        assertEquals(1, nodeOne.getVolumes().size());
        nodeOne.removeVolume(hostPath);
        assertEquals(0, nodeOne.getVolumes().size());
    }

    @Test
    void testRemoveNetwork() {
        String networkName = "testNetwork";
        nodeOne.addNetwork(networkName);
        assertEquals(1, nodeOne.getNetworks().size());
        nodeOne.removeNetwork(networkName);
        assertEquals(0, nodeOne.getNetworks().size());
    }

    @Test
    void testGetName() {
        assertEquals(nodeName, nodeOne.getName());
    }

    @Test
    void testGetBaseImage() {
        assertEquals(baseImage, nodeOne.getBaseImage());
    }

    @Test
    void testGetPackagesToInstall() {
        assertEquals(0, nodeOne.getPackagesToInstall().size());
        nodeOne.addPackage("curl");
        assertEquals(1, nodeOne.getPackagesToInstall().size());
    }

    @Test
    void testGetPortsToExpose() {
        assertEquals(0, nodeOne.getPortsToExpose().size());
        nodeOne.addPort("8080");
        assertEquals(1, nodeOne.getPortsToExpose().size());
    }

    @Test
    void testGetStartupCommands() {
        assertEquals(0, nodeOne.getStartupCommands().size());
        nodeOne.addStartupCommand("echo Hello World");
        assertEquals(1, nodeOne.getStartupCommands().size());
    }

    @Test
    void testGetEnvVars() {
        assertEquals(0, nodeOne.getEnvVars().size());
        nodeOne.addEnvVar("ENV_VAR", "value");
        assertEquals(1, nodeOne.getEnvVars().size());
    }

    @Test
    void testGetVolumes() {
        assertEquals(0, nodeOne.getVolumes().size());
        nodeOne.addVolume("/host/path", "/container/path");
        assertEquals(1, nodeOne.getVolumes().size());
    }

    @Test
    void testGetNetworks() {
        assertEquals(0, nodeOne.getNetworks().size());
        nodeOne.addNetwork("testNetwork");
        assertEquals(1, nodeOne.getNetworks().size());
    }

    @Test
    void testIsPriveledge() {
        assertEquals(false, nodeOne.isPriveledge());
        nodeOne.setPriveledge(true);
        assertEquals(true, nodeOne.isPriveledge());
    }

    @Test
    void testGetCapabilities() {
        assertEquals(0, nodeOne.getCapabilities().size());
        nodeOne.addCapability("CAP_NET_ADMIN");
        assertEquals(1, nodeOne.getCapabilities().size());
    }
}
