package network.simulation.test.Model;

import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Nodes.Device;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Test_Network {
    private final int NETONECAP = 254;
    private final int NETTWOCAP = 65534;
    private final int NETTHREECAP = 1;
    private final String NAMEONE = "one";
    private final String NAMETWO = "two";
    private final String NAMETHREE = "three";
    private Network testNetworkOne;
    private Network testNetworkTwo;
    private Network testNetworkThree;

    @BeforeEach
    void setup() {
        testNetworkOne = new Network(NAMEONE, "192.168.1.0/24");
        testNetworkTwo = new Network(NAMETWO, "10.0.0.0/16");
        testNetworkThree = new Network(NAMETHREE, "192.168.1.1/32");
    } 

    @Test
    void testCapacity() {
        int capacityOne = testNetworkOne.getCapacity();
        int capacityTwo = testNetworkTwo.getCapacity();
        int capacityThree = testNetworkThree.getCapacity();
        assertEquals(NETONECAP, capacityOne);
        assertEquals(NETTWOCAP, capacityTwo);
        assertEquals(NETTHREECAP, capacityThree);
    }

    @Test
    void testAddDevice() {
        Device deviceOne = new Device("device1", "ubuntu:latest");
        int numberOFDevicesAtInitialization = 0;
        assertEquals(numberOFDevicesAtInitialization, testNetworkOne.getDevicesInNetwork().size());
        testNetworkOne.addDevice(deviceOne);
        int numberOfDevicesAfterAdding = 1;
        assertEquals(numberOfDevicesAfterAdding, testNetworkOne.getDevicesInNetwork().size()); 
    }  
    
    @Test
    void testAddDeviceToFullNetwork() {
        for (int i = 0; i < NETONECAP; i++) {
            Device Device = new Device("Device" + i, "ubuntu:latest");
            testNetworkOne.addDevice(Device);
        }
        int numberOfDevicesAtFullCapacity = NETONECAP;
        assertEquals(numberOfDevicesAtFullCapacity, testNetworkOne.getDevicesInNetwork().size());
        
        Device extraDevice = new Device("ExtraDevice", "ubuntu:latest");
        testNetworkOne.addDevice(extraDevice);
        // Should not add the extra Device since the network is full
        assertEquals(numberOfDevicesAtFullCapacity, testNetworkOne.getDevicesInNetwork().size());
    }

    @Test
    void testRemoveDevice() {
        Device DeviceOne = new Device("Device1", "ubuntu:latest");
        testNetworkOne.addDevice(DeviceOne);
        int numberOfDevicesAfterAdding = 1;
        assertEquals(numberOfDevicesAfterAdding, testNetworkOne.getDevicesInNetwork().size());
        
        testNetworkOne.removeDevice(DeviceOne);
        int numberOfDevicesAfterRemoving = 0;
        assertEquals(numberOfDevicesAfterRemoving, testNetworkOne.getDevicesInNetwork().size());
    }

    @Test
    void testRemoveDeviceNotInNetwork() {
        Device DeviceOne = new Device("Device1", "ubuntu:latest");
        testNetworkOne.removeDevice(DeviceOne);
        // Should not throw an error, but the size should remain 0
        assertEquals(0, testNetworkOne.getDevicesInNetwork().size());
    }
    
    @Test
    void testNetworkName() {
        assertEquals(NAMEONE, testNetworkOne.getName());
        assertEquals(NAMETWO, testNetworkTwo.getName());
        assertEquals(NAMETHREE, testNetworkThree.getName());
    }
}
