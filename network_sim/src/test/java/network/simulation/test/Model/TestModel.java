package network.simulation.test.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestModel {
    Model model;
    String MODELNAME = "TestModel";
    String CUSTOMNETWORKNAME = "CustomNetwork";
    String CUSTOMNETWORKADDRESS = "192.168.0.0/24";
    String STANDARDNETWORKNAMEONE = "Network1";
    String STANDARDNETWORKNAMETWO = "Network2";
    String STANDARDDEVICENAMEONE = "Device1";
    String STANDARDDEVICENAMETWO = "Device2";

    @BeforeEach
    void setup() {
        model = new Model();
    }

    @Test
    void testSetGetName() {
        assertNotEquals(MODELNAME, model.getName());
        model.setName(MODELNAME);
        assertEquals(MODELNAME, model.getName());
    }

    @Test
    void testCreateNetwork() {
        model.createNetwork(CUSTOMNETWORKNAME, CUSTOMNETWORKADDRESS);
        assertEquals(1, model.getNetworkNames().size());
        assertEquals(CUSTOMNETWORKNAME, model.getNetworkNames().get(0));
    }

    @Test
    void testAddStandardNetwork() {
        int initialNetworkCount = model.getNetworkNames().size();
        model.addStandardNetwork();
        assertEquals(initialNetworkCount + 1, model.getNetworkNames().size());
        String newNetworkName = model.getNetworkNames().get(initialNetworkCount);
        assertEquals("Network" + (initialNetworkCount + 1), newNetworkName);
    }

    @Test  
    void testCreateDevice() {
        String deviceName = "TestDevice";
        String baseImage = "ubuntu:latest";
        model.createDevice(deviceName, baseImage);
        assertEquals(1, model.getUnassignedDevices().size());
        assertEquals(deviceName, model.getUnassignedDevices().get(0).getName());
        assertEquals(baseImage, model.getUnassignedDevices().get(0).getBaseImage());
    }

    @Test
    void testCreateStandardDevice() {
        int initialDeviceCount = model.getUnassignedDevices().size();
        model.addStandardDevice();
        assertEquals(initialDeviceCount + 1, model.getUnassignedDevices().size());
        String newDeviceName = model.getUnassignedDevices().get(initialDeviceCount).getName();
        assertEquals(STANDARDDEVICENAMEONE, newDeviceName);
    }

    @Test
    void testGetNeworkNames() {
        model.addStandardNetwork();
        model.addStandardNetwork();
        String firstNetworkName = model.getNetworkNames().get(0);
        String secondNetworkName = model.getNetworkNames().get(1);
        assertEquals(STANDARDNETWORKNAMEONE, firstNetworkName);
        assertEquals(STANDARDNETWORKNAMETWO, secondNetworkName);
    }

    @Test
    void testGetUnassignedDevices() {
        model.addStandardDevice();
        model.addStandardDevice();
        String firstDeviceName = model.getUnassignedDevices().get(0).getName();
        String secondDeviceName = model.getUnassignedDevices().get(1).getName();
        assertEquals(STANDARDDEVICENAMEONE, firstDeviceName);
        assertEquals(STANDARDDEVICENAMETWO, secondDeviceName);
    }

    @Test
    void testDeleteDevice() {
        model.addStandardDevice();
        String deviceName = model.getUnassignedDevices().get(0).getName();
        model.deleteDevice(deviceName, "unassigned");
        assertEquals(0, model.getUnassignedDevices().size());
    }

    @Test
    void testAssignDevice() {
        model.addStandardNetwork();
        model.addStandardDevice();
        String deviceName = model.getUnassignedDevices().get(0).getName();
        String networkName = model.getNetworkNames().get(0);
        model.assignDevice(deviceName, networkName);
        assertEquals(1, model.getDevicesInNetwork(networkName).size());
        assertEquals(deviceName, model.getDevicesInNetwork(networkName).get(0).getName());
    }
}
