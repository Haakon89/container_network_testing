package network.simulation.test.Controller;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Model;
import network.simulation.test.View.IView;

public class TestController {

    private final String PROJECT_NAME = "TestProject";
    private final String CUSTOM_NETWORK_NAME = "CustomNetwork";
    private final String CUSTOM_DEVICE_NAME = "customdevice";
    private final String STANDARD_NETWORK_NAME = "network1";
    private final String CUSTOM_NETWORK_ADRESS = "192.168.1.0/24";
    private final String STANDARD_DEVICE_NAME = "standard1";
    private final String STANDARD_DEVICE_DNS_TAG = "standard";
    private final String PATH = "/test/test"; 
    Model model;
    IView view;
    MainController controller;

    @BeforeEach
    void setup() {
        model = new Model();
        view = new DummyView(model);
        controller = new MainController(model, view);
    }

    @Test
    void TestOnCickNewProject() {
        assertNotEquals(model.getName(), PROJECT_NAME);
        controller.onClick("newProject", PROJECT_NAME, PATH);
        String actualPROJECT_NAME = model.getName();
        assertEquals(PROJECT_NAME, actualPROJECT_NAME);
    } 

    @Test
    void testOnClickAddStandardNetwork() {
        int initialNetworkCount = model.getNetworkNames().size();
        assertEquals(0, initialNetworkCount);
        controller.onClick("addStandardNetwork");
        int newNetworkCount = model.getNetworkNames().size();
        assertEquals(initialNetworkCount + 1, newNetworkCount);
        String newNetworkName = model.getNetworkNames().get(0);
        assertEquals(STANDARD_NETWORK_NAME, newNetworkName);
    }

    @Test
    void testOnClickAddCustomNetwork() {
        int initialNetworkCount = model.getNetworkNames().size();
        assertEquals(0, initialNetworkCount);
        controller.onClick("addCustomNetwork", CUSTOM_NETWORK_NAME, CUSTOM_NETWORK_ADRESS);
        int newNetworkCount = model.getNetworkNames().size();
        assertEquals(initialNetworkCount + 1, newNetworkCount);
        String newNetworkName = model.getNetworkNames().get(0);
        assertEquals(CUSTOM_NETWORK_NAME, newNetworkName);
    }

    @Test
    void testOnClickAddStandardDevice() {
        int initialDeviceCount = model.getUnassignedDevices().size();
        assertEquals(0, initialDeviceCount);
        controller.onClick("createDevice", STANDARD_DEVICE_DNS_TAG);
        int newDeviceCount = model.getUnassignedDevices().size();
        assertEquals(initialDeviceCount + 1, newDeviceCount);
        String newDeviceName = model.getUnassignedDevices().get(0).getName();
        assertEquals(STANDARD_DEVICE_NAME, newDeviceName);
    }

    @Test
    void testOnClickAddCustomDevice() {
        int initialDeviceCount = model.getUnassignedDevices().size();
        assertEquals(0, initialDeviceCount);
        controller.onClick("createCustomDevice", CUSTOM_DEVICE_NAME, CUSTOM_NETWORK_NAME);
        int newDeviceCount = model.getUnassignedDevices().size();
        assertEquals(initialDeviceCount + 1, newDeviceCount);
        String newDeviceName = model.getUnassignedDevices().get(0).getName();
        assertEquals(CUSTOM_DEVICE_NAME, newDeviceName);
    }
    
    @Test
    void testOnClickDeleteNetwork() {
        controller.onClick("addStandardNetwork");
        int initialNetworkCount = model.getNetworkNames().size();
        assertEquals(1, initialNetworkCount);
        controller.onClick("deleteNetwork", STANDARD_NETWORK_NAME);
        int newNetworkCount = model.getNetworkNames().size();
        assertEquals(initialNetworkCount - 1, newNetworkCount);
    }

    @Test
    void testOnClickRemoveDevice() {
        controller.onClick("createDevice", STANDARD_DEVICE_DNS_TAG);
        int initialDeviceCount = model.getUnassignedDevices().size();
        assertEquals(1, initialDeviceCount);
        String deviceName = model.getUnassignedDevices().get(0).getName();
        controller.onClick("deleteDevice", deviceName, "unassigned");
        int newDeviceCount = model.getUnassignedDevices().size();
        assertEquals(initialDeviceCount - 1, newDeviceCount);
    }
    
    private static class DummyView implements IView {
        Model model;
        public DummyView(Model model) {
            this.model = model;
        }
        @Override public void newProject(String name, String path) {
            model.setName(name);
            model.setPath(path);
            //removed the UI elements in the dummy version
            //rootItem.setValue("Project: " + name);
            //updateDisplay();
        }
        @Override public void closeProject() {}
        @Override public void setControllerAndHandler(IControllerView controller) {}
    }
}
