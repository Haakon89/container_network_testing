package network.simulation.test.Model;

import java.util.ArrayList;

import network.simulation.test.Model.Nodes.Device;

public interface IModelView {
    public String getName();
    public ArrayList<String> getNetworkNames();
    public ArrayList<Device> getUnassignedDevices();
    
}
