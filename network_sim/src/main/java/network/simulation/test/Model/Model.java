package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Nodes.Node;

public class Model implements IModel {

    String name;
    HashMap<String, Network> networks;
    HashMap<String, Node> nodes;

    public Model(String name) {
        this.name = name;
        this.networks = new HashMap<>();
        this.nodes = new HashMap<>();
    }

    public void createNetwork(String name, String adressRange) {
        Network network = new Network(name, adressRange);
        networks.put(name, network);
    }
    
    public Network getNetwork(String name) {
        return networks.get(name);
    }

    public void createNode(String name, String baseImage) {
        Node node = new Node(name, baseImage);
        nodes.put(name, node);
    }
    
    public void writeDockerfile(String path) {
        // Implementation for writing Dockerfile
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<String> getNetworks() {
        ArrayList<String> networkNames = new ArrayList<>();
        for (String networkName : networks.keySet()) {
            networkNames.add(networkName);
        }
        return networkNames;
    }

    @Override
    public HashMap<String, ArrayList<String>> getNodesRelations() {
        HashMap<String, ArrayList<String>> nodeNames = new HashMap<>();
        for (String nodeName : nodes.keySet()) {
            Node node = nodes.get(nodeName);
            ArrayList<String> networkList = new ArrayList<>();
            for (String network : node.getNetworks()) {
                networkList.add(network);
            }
            nodeNames.put(nodeName, networkList);
        }
        return nodeNames;
    }


}
