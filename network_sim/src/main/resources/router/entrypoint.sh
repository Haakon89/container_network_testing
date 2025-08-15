#!/usr/bin/env bash
set -euo pipefail

# Ensure IPv4 forwarding is enabled (compose also sets this as sysctl)
sysctl -w net.ipv4.ip_forward=1 >/dev/null

# Identify the two interfaces (eth0 will be the first network listed in compose,
# eth1 the second). If you prefer, you can hardcode them.
INT_A=${INT_A:-eth0}
INT_B=${INT_B:-eth1}

MODE="${1:-route}"  # "route" (pure routing) or "nat" (masquerade)

# Flush any existing rules (container-local)
iptables -F
iptables -t nat -F
iptables -X

# Always allow established/related traffic
iptables -A FORWARD -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT

if [ "$MODE" = "nat" ]; then
  # NAT each side to the other so peers don't need static routes
  iptables -A FORWARD -i "$INT_A" -o "$INT_B" -j ACCEPT
  iptables -A FORWARD -i "$INT_B" -o "$INT_A" -j ACCEPT

  # Masquerade out of each interface
  iptables -t nat -A POSTROUTING -o "$INT_B" -j MASQUERADE
  iptables -t nat -A POSTROUTING -o "$INT_A" -j MASQUERADE

  echo "[router] NAT mode enabled between $INT_A <-> $INT_B"
else
  # Pure routing (no NAT). Just allow forwarding both ways.
  iptables -A FORWARD -i "$INT_A" -o "$INT_B" -j ACCEPT
  iptables -A FORWARD -i "$INT_B" -o "$INT_A" -j ACCEPT
  echo "[router] Pure routing mode enabled between $INT_A <-> $INT_B"
fi

# Stay up
tail -f /dev/null
