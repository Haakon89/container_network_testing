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
