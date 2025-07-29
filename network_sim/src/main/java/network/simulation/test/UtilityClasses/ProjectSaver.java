package network.simulation.test.UtilityClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import network.simulation.test.Model.Model;
import network.simulation.test.Model.Nodes.DNSServer;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.PrinterDevice;
import network.simulation.test.Model.Nodes.StandardDevice;
import network.simulation.test.Model.Nodes.WebServer;

public class ProjectSaver {
    
    public static void saveProject(Model model) throws IOException {
        Path projectDir = Path.of(model.getPath());

        Gson gson = getCustomGson();
        for (Map.Entry<String, Device> entry : model.getDevices().entrySet()) {
            System.out.println("Saving device: " + entry.getKey() + " -> " + entry.getValue().getClass().getSimpleName());
        }
        String json = gson.toJson(model);

        Path projectJson = projectDir.resolve("project.json");
        Files.writeString(projectJson, json);
    }

    private static Gson getCustomGson() {
        RuntimeTypeAdapterFactory<Device> adapter = RuntimeTypeAdapterFactory
            .of(Device.class, "type")
            .registerSubtype(StandardDevice.class, "standard")
            .registerSubtype(DNSServer.class, "dns")
            .registerSubtype(WebServer.class, "webserver")
            .registerSubtype(PrinterDevice.class, "printer");
        
        return new GsonBuilder()
            .registerTypeAdapterFactory(adapter)
            .setPrettyPrinting()
            .create();
    }
}
