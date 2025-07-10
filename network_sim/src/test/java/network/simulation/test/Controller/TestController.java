package network.simulation.test.Controller;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Model;
import network.simulation.test.View.IView;

public class TestController {
    String projectName = "TestProject";
    String customNetworkName = "CustomNetwork";
    String customDeviceName = "CustomDevice";
    String standardNetworkName = "Network1";
    String customNetworkAddress = "192.168.1.0/24";
    String standardDeviceName = "Device1";
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
        assertNotEquals(model.getName(), projectName);
        controller.onClick("newProject", projectName);
        String actualProjectName = model.getName();
        assertEquals(projectName, actualProjectName);
    } 

    @Test
    void testOnClickAddStandardNetwork() {
        int initialNetworkCount = model.getNetworkNames().size();
        assertEquals(0, initialNetworkCount);
        controller.onClick("addStandardNetwork");
        int newNetworkCount = model.getNetworkNames().size();
        assertEquals(initialNetworkCount + 1, newNetworkCount);
        String newNetworkName = model.getNetworkNames().get(0);
        assertEquals(standardNetworkName, newNetworkName);
    }

    @Test
    void testOnClickAddCustomNetwork() {
        int initialNetworkCount = model.getNetworkNames().size();
        assertEquals(0, initialNetworkCount);
        controller.onClick("addCustomNetwork", customNetworkName, customNetworkAddress);
        int newNetworkCount = model.getNetworkNames().size();
        assertEquals(initialNetworkCount + 1, newNetworkCount);
        String newNetworkName = model.getNetworkNames().get(0);
        assertEquals(customNetworkName, newNetworkName);
    }

    @Test
    void testOnClickAddStandardDevice() {
        int initialDeviceCount = model.getUnassignedDevices().size();
        assertEquals(0, initialDeviceCount);
        controller.onClick("addStandardDevice", standardNetworkName);
        int newDeviceCount = model.getUnassignedDevices().size();
        assertEquals(initialDeviceCount + 1, newDeviceCount);
        String newDeviceName = model.getUnassignedDevices().get(0).getName();
        assertEquals(standardDeviceName, newDeviceName);
    }

    @Test
    void testOnClickAddCustomDevice() {
        int initialDeviceCount = model.getUnassignedDevices().size();
        assertEquals(0, initialDeviceCount);
        controller.onClick("addCustomDevice", customDeviceName, customNetworkName);
        int newDeviceCount = model.getUnassignedDevices().size();
        assertEquals(initialDeviceCount + 1, newDeviceCount);
        String newDeviceName = model.getUnassignedDevices().get(0).getName();
        assertEquals(customDeviceName, newDeviceName);
    }
    /*
    @Test
    void testOnClickRemoveNetwork() {
        controller.onClick("addStandardNetwork");
        int initialNetworkCount = model.getNetworkNames().size();
        assertEquals(1, initialNetworkCount);
        controller.onClick("removeNetwork", standardNetworkName);
        int newNetworkCount = model.getNetworkNames().size();
        assertEquals(initialNetworkCount - 1, newNetworkCount);
    }

    @Test
    void testOnClickRemoveDevice() {
        controller.onClick("addStandardDevice", standardNetworkName);
        int initialDeviceCount = model.getUnassignedDevices().size();
        assertEquals(1, initialDeviceCount);
        String deviceName = model.getUnassignedDevices().get(0).getName();
        controller.onClick("removeDevice", deviceName);
        int newDeviceCount = model.getUnassignedDevices().size();
        assertEquals(initialDeviceCount - 1, newDeviceCount);
    }
    */
    private static class DummyView implements IView {
        Model model;
        public DummyView(Model model) {
            this.model = model;
        }
        @Override public void newProject(String name) {
            model.setName(name);
            //removed the UI elements in the dummy version
            //rootItem.setValue("Project: " + name);
            //updateDisplay();
        }
        @Override public void openProject(String name) {}
        @Override public void saveProject(String name) {}
        @Override public void saveProjectAs(String name) {}
        @Override public void closeProject(String name) {}
        @Override public void setController(IViewController controller) {}
    }
}
