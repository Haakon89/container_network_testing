package network.simulation.test.Model;

import org.junit.jupiter.api.Test;

import network.simulation.test.Model.Nodes.Node;

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
    void testAddNode() {
        Node nodeOne = new Node("Node1", "ubuntu:latest");
        int numberOFNodesAtInitialization = 0;
        assertEquals(numberOFNodesAtInitialization, testNetworkOne.getNodesInNetwork().size());
        testNetworkOne.addNode(nodeOne);
        int numberOfNodesAfterAdding = 1;
        assertEquals(numberOfNodesAfterAdding, testNetworkOne.getNodesInNetwork().size()); 
    }  
    
    @Test
    void testAddNodeToFullNetwork() {
        for (int i = 0; i < NETONECAP; i++) {
            Node node = new Node("Node" + i, "ubuntu:latest");
            testNetworkOne.addNode(node);
        }
        int numberOfNodesAtFullCapacity = NETONECAP;
        assertEquals(numberOfNodesAtFullCapacity, testNetworkOne.getNodesInNetwork().size());
        
        Node extraNode = new Node("ExtraNode", "ubuntu:latest");
        testNetworkOne.addNode(extraNode);
        // Should not add the extra node since the network is full
        assertEquals(numberOfNodesAtFullCapacity, testNetworkOne.getNodesInNetwork().size());
    }

    @Test
    void testRemoveNode() {
        Node nodeOne = new Node("Node1", "ubuntu:latest");
        testNetworkOne.addNode(nodeOne);
        int numberOfNodesAfterAdding = 1;
        assertEquals(numberOfNodesAfterAdding, testNetworkOne.getNodesInNetwork().size());
        
        testNetworkOne.removeNode(nodeOne);
        int numberOfNodesAfterRemoving = 0;
        assertEquals(numberOfNodesAfterRemoving, testNetworkOne.getNodesInNetwork().size());
    }

    @Test
    void testRemoveNodeNotInNetwork() {
        Node nodeOne = new Node("Node1", "ubuntu:latest");
        testNetworkOne.removeNode(nodeOne);
        // Should not throw an error, but the size should remain 0
        assertEquals(0, testNetworkOne.getNodesInNetwork().size());
    }
    
    @Test
    void testNetworkName() {
        assertEquals(NAMEONE, testNetworkOne.getName());
        assertEquals(NAMETWO, testNetworkTwo.getName());
        assertEquals(NAMETHREE, testNetworkThree.getName());
    }
}
