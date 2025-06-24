package network.simulation.test.Model;

import java.util.ArrayList;

import network.simulation.test.Model.Nodes.Device;

public interface IModel {
    public String getName();
    public ArrayList<String> getNetworks();
    public void addStandardNetwork();
    public void createNetwork(String name, String string2);
    public void addStandardDevice();
    public void createDevice(String name, String baseImage);
    public ArrayList<Device> getUnassignedDevices();
    public void deleteDevice(String device, String home);
    public void assignDevice(String device, String network);
}
