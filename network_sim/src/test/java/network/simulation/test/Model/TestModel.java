package network.simulation.test.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AfterEach;

public class TestModel {
    Model model;
    String MODEL_NAME = "TestModel";
    String CUSTOM_NETWORK_NAME = "CustomNetwork";
    String CUSTOM_NETWORK_ADDRESS = "192.168.0.0/24";
    String STANDARD_NETWORK_NAME_ONE = "network1";
    String STANDARD_NETWORK_NAME_TWO = "network2";
    String STANDARD_DEVICE_NAME_ONE = "standard1";
    String STANDARD_DEVICE_NAME_TWO = "standard2";
    String DEVICETYPE_STANDARD = "standard";
    String DEVICETYPE_DNS = "dns";
    String DEVICETYPE_WEB = "web";

    @BeforeEach
    void setup() {
        model = new Model();
    }

    @AfterEach
    void reset() {
        model.resetdeviceFactory();
    }

    @Test
    void testSetGetName() {
        assertNotEquals(MODEL_NAME, model.getName());
        model.setName(MODEL_NAME);
        assertEquals(MODEL_NAME, model.getName());
    }

    @Test
    void testCreateNetwork() {
        model.createNetwork(CUSTOM_NETWORK_NAME, CUSTOM_NETWORK_ADDRESS);
        assertEquals(1, model.getNetworkNames().size());
        assertEquals(CUSTOM_NETWORK_NAME, model.getNetworkNames().get(0));
    }

    @Test
    void testAddStandardNetwork() {
        int initialNetworkCount = model.getNetworkNames().size();
        model.addStandardNetwork();
        assertEquals(initialNetworkCount + 1, model.getNetworkNames().size());
        String newNetworkName = model.getNetworkNames().get(initialNetworkCount);
        assertEquals("network" + (initialNetworkCount + 1), newNetworkName);
    }

    @Test  
    void testCreateDevice() {
        model.createDevice(DEVICETYPE_STANDARD);
        assertEquals(1, model.getUnassignedDevices().size());
        assertEquals("standard1", model.getUnassignedDevices().get(0).getName());
    }

    @Test
    void testCreateStandardDevice() {
        int initialDeviceCount = model.getUnassignedDevices().size();
        model.createDevice(DEVICETYPE_STANDARD);
        model.createDevice(DEVICETYPE_STANDARD);
        assertEquals(initialDeviceCount + 2, model.getUnassignedDevices().size());
        String newDeviceNameOne = model.getUnassignedDevices().get(0).getName();
        String newDeviceNameTwo = model.getUnassignedDevices().get(1).getName();
        assertEquals(STANDARD_DEVICE_NAME_ONE, newDeviceNameOne);
        assertEquals(STANDARD_DEVICE_NAME_TWO, newDeviceNameTwo);
    }

    @Test
    void testGetNeworkNames() {
        model.addStandardNetwork();
        model.addStandardNetwork();
        String firstNetworkName = model.getNetworkNames().get(0);
        String secondNetworkName = model.getNetworkNames().get(1);
        assertEquals(STANDARD_NETWORK_NAME_ONE, firstNetworkName);
        assertEquals(STANDARD_NETWORK_NAME_TWO, secondNetworkName);
    }

    @Test
    void testGetUnassignedDevices() {
        model.createDevice(DEVICETYPE_STANDARD);
        model.createDevice(DEVICETYPE_STANDARD);
        String firstDeviceName = model.getUnassignedDevices().get(0).getName();
        String secondDeviceName = model.getUnassignedDevices().get(1).getName();
        assertEquals(STANDARD_DEVICE_NAME_ONE, firstDeviceName);
        assertEquals(STANDARD_DEVICE_NAME_TWO, secondDeviceName);
    }

    @Test
    void testDeleteDevice() {
        model.createDevice(DEVICETYPE_STANDARD);
        String deviceName = model.getUnassignedDevices().get(0).getName();
        model.deleteDevice(deviceName, "unassigned");
        assertEquals(0, model.getUnassignedDevices().size());
    }

    @Test
    void testAssignDevice() {
        model.addStandardNetwork();
        model.createDevice(DEVICETYPE_STANDARD);
        String deviceName = model.getUnassignedDevices().get(0).getName();
        String networkName = model.getNetworkNames().get(0);
        model.assignDevice(deviceName, networkName);
        assertEquals(1, model.getDevicesInNetwork(networkName).size());
        assertEquals(deviceName, model.getDevicesInNetwork(networkName).get(0).getName());
    }
}
