package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Nodes.Node;

public interface IModel {
    public String getName();
    public ArrayList<String> getNetworks();
    public HashMap<String, ArrayList<String>> getNodesRelations();
    public void addStandardNetwork();
    public void createNetwork(String name, String string2);
    public void addStandardNode();
    public void createNode(String name, String baseImage);
    public ArrayList<Node> getUnassignedNodes();
    public void deleteNode(String string);
    public void assignNode(String string, String string2);
}
