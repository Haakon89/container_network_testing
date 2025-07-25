package network.simulation.test.UtilityClasses;
import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.file.Path;


public class TestSaveAndLoad {
    Model testModel;
    String projectName = "TestProject";
    String projectPath = "network_sim/src/test/java/network/simulation/test/UtilityClasses/";

    @Test
    public void testSavingandLoadingModel() {
        System.out.println("Absolute project path: " + Path.of(projectPath).toAbsolutePath());
        testModel = new Model();
        Model emptyModel = new Model();
        testModel.setName(projectName);
        testModel.setPath(projectPath);
        testModel.addStandardNetwork();
        testModel.createDevice("standard");
        testModel.assignDevice("standard1", "network1");
        assertNotEquals(testModel, emptyModel);
        testModel.saveModel();
        try {
            Model loadedModel = ProjectLoader.loadProject(Path.of(projectPath));
            assertEquals(testModel, loadedModel);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
