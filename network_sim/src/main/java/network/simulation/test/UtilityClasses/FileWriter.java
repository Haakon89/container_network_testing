package network.simulation.test.UtilityClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import network.simulation.test.Model.Network;
import network.simulation.test.Model.Nodes.Device;
import network.simulation.test.Model.Nodes.RouterDevice;

public class FileWriter {
    //This class will contain all instances of writing text to a file which can then be used by the other classes to generate the files that they need

    //Docker files

    /**
     * Generates a Docker Compose file for the current model.
     * It includes all networks and their devices, along with their configurations.
     * The file is written to the specified path.
     * @param path the path where the docker-compose.yml file will be created
     * @param networks the networks that have the information that we need
     */
    public static void generateDockerCompose(Path path, HashMap<String, Network> networks, ArrayList<RouterDevice> routers) {
        StringBuilder compose = new StringBuilder();
        compose.append("services:\n");
        for (RouterDevice router : routers) {
            compose.append("  ").append(router.getName()).append(":\n");
            compose.append("    build: ./" + router.getName() + "\n");
            compose.append("    container_name: ").append(router.getName()).append("\n");
            compose.append("    cap_add: [ NET_ADMIN ]\n");
            compose.append("    sysctls:\n");
            compose.append("      net.ipv4.ip_forward: \"1\"\n");
            compose.append("    command: [\"nat\"]\n");
            compose.append("    networks:\n");
            for (Network network : router.getConnectedNetworks()) {
                compose.append("      ").append(network.getName()).append(":\n");
                compose.append("        ipv4_address: ").append(network.getRouterAddress()).append("\n");
            }
            compose.append("    restart: unless-stopped\n\n");
        }
        for (Network network : networks.values()) {
            compose.append(network.getComposeInfo());
        }

        compose.append("networks:\n");

        for (Network network : networks.values()) {
            compose.append("  ").append(network.getName()).append(":\n");
            compose.append("    driver: bridge\n");
            compose.append("    ipam:\n");
            compose.append("      config:\n");
            compose.append("        - subnet: ").append(network.getAddressRange()).append("\n");
            compose.append("          gateway: ").append(network.getGateway()).append("\n");
        }

        try {
            Files.writeString(path, compose.toString());
        } catch (IOException e) {
            System.err.println("Error writing docker-compose.yml: " + e.getMessage());
        }
    }

    //DNS files

    /**
     * part of the DNS lookup table assigning a label to each device given its job on the network.
     * @param devicesInNetwork the devices inside the network
     * @param domain name of the network
     * @param path path to where the files should be stored
     * @return the name of the file that we stored to be used in the named.conf.local file
     * @throws IOException
     */
    public static String writeForwardZone(ArrayList<Device> devicesInNetwork, String domain, Path path) throws IOException {
        Path zonePath = Paths.get(path + "/zones/");
        Files.createDirectories(zonePath);
        StringBuilder zone = new StringBuilder();
        zone.append("$TTL 604800\n");
        zone.append("@ IN SOA ns1.").append(domain + ".local").append(". admin.").append(domain + ".local").append(". (\n");
        zone.append("    2     ; Serial\n");
        zone.append("    604800 ; Refresh\n");
        zone.append("    86400  ; Retry\n");
        zone.append("    2419200 ; Expire\n");
        zone.append("    604800 ) ; Negative Cache TTL\n\n");

        zone.append("@ IN NS ns1.").append(domain).append(".\n");

        for (Device d : devicesInNetwork) {
            if (d.getIpAddress() != null) {
                zone.append(d.getDNSLabel()).append(" IN A ").append(d.getIpAddress()).append("\n");
            }
        }
        System.out.println(zone.toString());
        String title = "db." + domain + ".local";
        Files.writeString(zonePath.resolve(title), zone.toString());
        System.out.println("Wrote zone file to: " + zonePath.toAbsolutePath());
        return title;
    }

    /**
     * part of the DNS lookup table showing which endpoint each job is located at
     * @param devices devices in the network
     * @param domain name of the network
     * @param path path to where the fiel should be stored
     * @param addressRange address range of the network
     * @return the name of the file that we stored to be used in the named.conf.local file
     * @throws IOException
     */
    public static String writeReverseZone(ArrayList<Device> devices, String domain, Path path, String addressRange) throws IOException {
        Path zonePath = Paths.get(path + "/zones/");
        Files.createDirectories(zonePath);
        StringBuilder zone = new StringBuilder();
        zone.append("$TTL 604800\n");
        zone.append("@ IN SOA ns1." + domain + ".local. admin." + domain + ".local. (\n");
        zone.append("    2     ; Serial\n");
        zone.append("    604800 ; Refresh\n");
        zone.append("    86400  ; Retry\n");
        zone.append("    2419200 ; Expire\n");
        zone.append("    604800 ) ; Negative Cache TTL\n\n");
    
        zone.append("@ IN NS ns1." + domain + ".local.\n");
    
        for (Device d : devices) {
            String ip = d.getIpAddress();
            if (ip != null) {
                String lastOctet = ip.substring(ip.lastIndexOf('.') + 1);
                zone.append(lastOctet).append(" IN PTR ").append(d.getDNSLabel()).append("." + domain + ".local.\n");
            }
        }
        String[] parts = addressRange.split("/");
        String[] octets = parts[0].split("\\.");

        // For /24, we reverse the first 3 octets
        String title = "db." + octets[2] + "." + octets[1] + "." + octets[0] + ".in-addr.arpa";
        System.out.println(zone.toString());
        Files.writeString(zonePath.resolve(title), zone.toString());
        System.out.println("Wrote zone file to: " + zonePath.toAbsolutePath());
        return title;
    }
 
    /**
     * writer for the DNS named.conf.local file, used for finding the other relevant files for the DNS
     * @param domain name of the network
     * @param path path to where the file should be stored
     * @param filenameOne name of the db.domain.local file
     * @param filenameTWO name of the db.addressRange.in-addr.arpa file
     * @throws IOException
     */
    public static void writeNamedConfLocal(String domain, Path path, String filenameOne, String filenameTWO) throws IOException {
        StringBuilder config = new StringBuilder();
        config.append("zone \"" + domain + ".local\" {\n");
        config.append("    type master;\n");
        config.append("    file \"/etc/bind/zones/" + filenameOne + "\";\n");
        config.append("};\n\n");
    
        config.append("zone \"" + filenameTWO.substring(3) + "\" {\n");
        config.append("    type master;\n");
        config.append("    file \"/etc/bind/zones/" + filenameTWO + "\";\n");
        config.append("};\n");
        System.out.println(config.toString());
        Files.writeString(path.resolve("named.conf.local"), config.toString());
        System.out.println("Wrote zone file to: " + path.toAbsolutePath());
    }
}
