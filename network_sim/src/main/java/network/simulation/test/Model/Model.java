package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Nodes.Node;

public class Model implements IModel {

    String name;
    HashMap<String, Network> networks;
    HashMap<String, Node> nodes;
    ArrayList<Node> unassignedNodes;

    public Model() {
        this.name = "";
        this.networks = new HashMap<>();
        this.nodes = new HashMap<>();
        this.unassignedNodes = new ArrayList<>();
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void createNetwork(String name, String adressRange) {
        Network network = new Network(name, adressRange);
        this.networks.put(name, network);
        System.out.println("Network " + name + " created with address range " + adressRange);
        System.out.println("Available networks: " + networks.keySet());
    }
    
    public Network getNetwork(String name) {
        return networks.get(name);
    }

    @Override
    public void createNode(String name, String baseImage) {
        Node node = new Node(name, baseImage);
        this.unassignedNodes.add(node);
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

    @Override
    public void addStandardNetwork() {
        String number = String.valueOf(networks.size() + 1);
        String name = "Network" + number;
        String adressRange = "192.168." + number + ".0/24"; // Default address range for standard networks
        Network network = new Network(name, adressRange);
        this.networks.put(name, network);
        System.out.println("Network " + name + " created with address range " + adressRange);
        System.out.println("Available networks: " + networks.keySet());
    }

    @Override
    public void addStandardNode() {
        String number = String.valueOf(unassignedNodes.size() + 1);
        String name = "Node" + number;
        String baseImage = "ubuntu:latest"; // Default base image for standard nodes
        Node node = new Node(name, baseImage);
        this.unassignedNodes.add(node);
    }

    @Override
    public ArrayList<Node> getUnassignedNodes() {
        return this.unassignedNodes;
    }

    @Override
    public void deleteNode(String string) {
        
    }

    @Override
    public void assignNode(String string, String string2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assignNode'");
    }

    


}
