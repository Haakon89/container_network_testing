package network.simulation.test.Model;

import java.util.ArrayList;
import java.util.HashMap;

public interface IModel {
    public String getName();
    public ArrayList<String> getNetworks();
    HashMap<String, ArrayList<String>> getNodesRelations();
}
