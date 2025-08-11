package network.simulation.test.Model.Nodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FirewallDevice extends Device {
    private String inputPolicy = "DROP";
    private String outputPolicy = "ACCEPT";
    private String forwardPolicy = "DROP";

    // Store just enough to build rules.v4 later
    private final ArrayList<String> forwardAllows = new ArrayList<>();
    private final ArrayList<String> forwardDenies  = new ArrayList<>();
    private final ArrayList<String> rawNatRules    = new ArrayList<>();

    public FirewallDevice(String name) {
        super(name);
        this.DNSLabel = name;
        this.setBaseImage("ubuntu:latest");
        installPackage("iptables");
        installPackage("iproute2");
        installService("firewall");
    }

    public void setDefaultPolicies(String input, String output, String forward) {
        this.inputPolicy = input;
        this.outputPolicy = output;
        this.forwardPolicy = forward;
    }

    // These "add" methods don't execute anything; they just record desired rules.
    public void allowForward(String srcCidr, String dstCidr, String proto, Integer dport) {
        forwardAllows.add(buildRuleSpec(srcCidr, dstCidr, proto, dport, "ACCEPT"));
    }

    public void denyForward(String srcCidr, String dstCidr, String proto, Integer dport) {
        forwardDenies.add(buildRuleSpec(srcCidr, dstCidr, proto, dport, "DROP"));
    }

    public void enableMasquerade(String outIface) {
        rawNatRules.add("-A POSTROUTING -o " + outIface + " -j MASQUERADE");
    }

    private String buildRuleSpec(String src, String dst, String proto, Integer dport, String target) {
        StringBuilder sb = new StringBuilder("-A FORWARD");
        if (src != null) sb.append(" -s ").append(src);
        if (dst != null) sb.append(" -d ").append(dst);
        if (proto != null) sb.append(" -p ").append(proto);
        if (dport != null && dport > 0) sb.append(" --dport ").append(dport);
        sb.append(" -j ").append(target);
        return sb.toString();
    }

    @Override
    public String generateDockerfile() {
        StringBuilder df = new StringBuilder();
        df.append("FROM ").append(this.baseImage).append("\n\n")
          .append("RUN apt-get update && apt-get install -y \\\n")
          .append("    iptables iproute2 \\\n")
          .append("    && apt-get clean\n\n")
          .append("WORKDIR /root\n")
          .append("COPY rules.v4 /etc/iptables/rules.v4\n")
          // Apply rules on start, then stay alive (your tooling expects persistent containers)
          .append("CMD [\"sh\", \"-c\", \"echo 1 > /proc/sys/net/ipv4/ip_forward && iptables-restore < /etc/iptables/rules.v4 && tail -f /dev/null\"]\n");
        return df.toString();
    }

    @Override
    public void writeDockerfileToFile(Path basePath) {
        try {
            Path deviceDir = basePath.resolve(this.name);
            Files.createDirectories(deviceDir);

            // 1) Dockerfile
            Files.writeString(deviceDir.resolve("Dockerfile"), generateDockerfile());

            // 2) rules.v4 (iptables-restore format)
            Files.writeString(deviceDir.resolve("rules.v4"), buildRulesV4());
        } catch (IOException e) {
            System.err.println("Error writing Firewall files: " + e.getMessage());
        }
    }

    private String buildRulesV4() {
        StringBuilder sb = new StringBuilder();
        sb.append("*filter\n");
        sb.append(":INPUT ").append(inputPolicy).append(" [0:0]\n");
        sb.append(":FORWARD ").append(forwardPolicy).append(" [0:0]\n");
        sb.append(":OUTPUT ").append(outputPolicy).append(" [0:0]\n");

        // Always sensible base allowances
        sb.append("-A INPUT -i lo -j ACCEPT\n");
        sb.append("-A OUTPUT -o lo -j ACCEPT\n");
        sb.append("-A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT\n");
        sb.append("-A OUTPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT\n");

        // User rules
        for (String r : forwardAllows) sb.append(r).append("\n");
        for (String r : forwardDenies)  sb.append(r).append("\n");
        sb.append("COMMIT\n");

        // NAT table if needed
        if (!rawNatRules.isEmpty()) {
            sb.append("*nat\n");
            sb.append(":PREROUTING ACCEPT [0:0]\n");
            sb.append(":INPUT ACCEPT [0:0]\n");
            sb.append(":OUTPUT ACCEPT [0:0]\n");
            sb.append(":POSTROUTING ACCEPT [0:0]\n");
            for (String r : rawNatRules) sb.append(r).append("\n");
            sb.append("COMMIT\n");
        }

        return sb.toString();
    }

    @Override
    public void start() { setRunning(true); }

    @Override
    public void stop() { setRunning(false); }

    @Override
    public ArrayList<String> getDisplayInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("Device Name: " + getName());
        info.add("Type: Firewall");
        info.add("Policies: INPUT=" + inputPolicy + ", OUTPUT=" + outputPolicy + ", FORWARD=" + forwardPolicy);
        info.add("Allow rules: " + forwardAllows.size());
        info.add("Deny rules: " + forwardDenies.size());
        info.add("NAT rules: " + rawNatRules.size());
        return info;
    }
}
