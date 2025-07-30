#!/bin/bash

mkdir -p /var/run/cups /run/cups /etc/cups/ppd

# Start CUPS in background
/usr/sbin/cupsd
echo "[entrypoint] cupsd started..."

# Wait until CUPS is ready (cups.sock exists or HTTP responds)
for i in {1..10}; do
  if lpstat -h localhost:631 -r > /dev/null 2>&1; then
    echo "[entrypoint] CUPS is ready."
    break
  fi
  echo "[entrypoint] Waiting for CUPS to start..."
  sleep 1
done

# Register virtual printer
echo "[entrypoint] Adding printer..."
lpadmin -h localhost:631 -p fakeprinter -E -v file:/dev/null -m drv:///sample.drv/generic.ppd

# Keep CUPS running in foreground
exec /usr/sbin/cupsd -f
