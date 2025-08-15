package network.simulation.test.UtilityClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import network.simulation.test.Model.Model;
import network.simulation.test.Model.Nodes.DNSServer;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.FirewallDevice;
import network.simulation.test.Model.Nodes.PrinterDevice;
import network.simulation.test.Model.Nodes.RouterDevice;
import network.simulation.test.Model.Nodes.StandardDevice;
import network.simulation.test.Model.Nodes.WebServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectLoader {
    public static Model loadProject(Path projectRoot) throws IOException {
        Path jsonPath = projectRoot.resolve("project.json");

        if (!Files.exists(jsonPath)) {
            throw new IOException("project.json not found at: " + jsonPath);
        }

        String json = Files.readString(jsonPath);

        Gson gson = getCustomGson();

        Model model = gson.fromJson(json, Model.class);

        model.setPath(projectRoot.toString());

        return model;
    }

    public static Gson getCustomGson() {
        RuntimeTypeAdapterFactory<Device> adapter = RuntimeTypeAdapterFactory
            .of(Device.class, "type")
            .registerSubtype(StandardDevice.class, "standard")
            .registerSubtype(DNSServer.class, "dns")
            .registerSubtype(WebServer.class, "webserver")
            .registerSubtype(PrinterDevice.class, "printer")
            .registerSubtype(FirewallDevice.class, "firewall")
            .registerSubtype(RouterDevice.class, "router");
        
        return new GsonBuilder()
            .registerTypeAdapterFactory(adapter)
            .setPrettyPrinting()
            .create();
    }
}
