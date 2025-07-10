package network.simulation.test.Model;
import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Nodes.CustomDevice;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.StandardDevice;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDevice {
    private String nodeNameOne = "Node1";
    private String nodeNameTwo = "Node2";
    private String baseImage = "ubuntu:latest";
    private Device nodeOne;
    private Device nodeTwo;
    
    @BeforeEach
    void setup() {
        nodeOne = new CustomDevice(nodeNameOne, baseImage);
        nodeTwo = new StandardDevice(nodeNameTwo);
    }
    
}