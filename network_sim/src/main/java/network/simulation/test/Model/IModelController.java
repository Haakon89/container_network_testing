package network.simulation.test.Model;

public interface IModelController {
    public void addStandardNetwork();
    public void createNetwork(String name, String string2);
    public void addStandardDevice();
    public void createDevice(String name, String baseImage);
    public void deleteDevice(String device, String home);
    public void assignDevice(String device, String network);
}
