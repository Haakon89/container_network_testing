package network.simulation.test.UtilityClasses;

import java.util.ArrayList;

public class SubNetSplitter {
    /**
     * Splits a given CIDR block into n subnets.
     *
     * @param cidr The CIDR block to split (e.g., "
     */
    public static ArrayList<String> splitNetwork(String cidr, int n) {
        ArrayList<String> result = new ArrayList<>();
        String[] parts = cidr.split("/");
        String baseIp = parts[0];
        int prefix = Integer.parseInt(parts[1]);

        // Convert IP to int
        int baseInt = ipToInt(baseIp);

        // Calculate how many extra bits we need for n subnets
        int bitsNeeded = (int) Math.ceil(Math.log(n) / Math.log(2));
        int newPrefix = prefix + bitsNeeded;

        if (newPrefix > 32) {
            throw new IllegalArgumentException("Too many subnets for given block");
        }

        int blockSize = 1 << (32 - newPrefix); // size of each new subnet

        for (int i = 0; i < n; i++) {
            int subnetBase = baseInt + (i * blockSize);
            result.add(intToIp(subnetBase) + "/" + newPrefix);
        }

        return result;
    }

    private static int ipToInt(String ip) {
        String[] octets = ip.split("\\.");
        int result = 0;
        for (String octet : octets) {
            result = (result << 8) | Integer.parseInt(octet);
        }
        return result;
    }

    private static String intToIp(int ipInt) {
        return String.format("%d.%d.%d.%d",
                (ipInt >> 24) & 0xFF,
                (ipInt >> 16) & 0xFF,
                (ipInt >> 8) & 0xFF,
                ipInt & 0xFF);
    }
}
