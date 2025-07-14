package network.simulation.test.Model.Nodes;

import java.nio.file.Path;
import java.util.ArrayList;

public class CustomDevice extends Device {

    public CustomDevice(String name, String baseImage) {
        super(name);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }

    @Override
    public String generateDockerfile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateDockerfile'");
    }

    @Override
    public void writeDockerfileToFile(Path filepath) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeDockerfileToFile'");
    }

    @Override
    public ArrayList<String> getDisplayInfo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDisplayInfo'");
    }

    
}
