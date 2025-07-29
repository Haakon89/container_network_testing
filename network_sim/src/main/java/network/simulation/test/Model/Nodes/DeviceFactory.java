package network.simulation.test.Model.Nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DeviceFactory {

    private static final Map<String, Supplier<Device>> deviceConstructors = new HashMap<>();
    private static final Map<String, Integer> typeCounters = new HashMap<>();

    static {
        deviceConstructors.put("standard", () -> new StandardDevice(generateName("standard")));
        deviceConstructors.put("dns", () -> new DNSServer(generateName("dns")));
        deviceConstructors.put("web", () -> new WebServer(generateName("web")));
        deviceConstructors.put("printer", () -> new PrinterDevice(generateName("printer")));
        // Add more device types here
    }

    public static Device buildDevice(String type) {
        Supplier<Device> constructor = deviceConstructors.get(type.toLowerCase());
        if (constructor == null) {
            throw new IllegalArgumentException("Unknown device type: " + type);
        }
        return constructor.get();
    }

    private static String generateName(String prefix) {
        int count = typeCounters.getOrDefault(prefix, 1);
        typeCounters.put(prefix, count + 1);
        return prefix + count;
    }

    public static Set<String> getAvailableTypes() {
        return deviceConstructors.keySet();
    }

    public static void resetCounters() {
        typeCounters.clear(); // Optional method to reset between projects/tests
    }
}
