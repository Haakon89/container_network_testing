FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    cups cups-bsd cups-client cups-filters ghostscript \
    printer-driver-gutenprint locales curl tzdata ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# CUPS runs as root inside the container but limits itself to lp group.
# Create dirs for persistence & logs
RUN mkdir -p /var/log/cups /var/spool/cups /etc/cups/ppd && \
    chown -R root:lp /var/log/cups /var/spool/cups /etc/cups/ppd

# Allow remote admin/browsing inside your lab network
# We'll drop a permissive cupsd.conf at runtime (mounted volume).

# Simple backend to “capture” jobs: /usr/lib/cups/backend/capture
RUN cat >/usr/lib/cups/backend/capture <<'SH'
#!/bin/sh
# CUPS Backend prototype: capture://<name>
# URL example: capture://SimPrinter
# Writes job data + minimal metadata to /captured
OUTDIR="/captured"
mkdir -p "$OUTDIR"
if [ "$1" = "probe" ]; then
  printf "network capture \"Unknown\" \"Capture Backend\"\n"
  exit 0
fi
JOBID="$CUPS_JOBID"
JOBFILE="$OUTDIR/job_${JOBID}_$(date +%s).bin"
cat > "$JOBFILE"
# Minimal successful exit: print attributes for scheduler
echo "ATTR: printer-state=3"
echo "ATTR: printer-state-message=printed"
exit 0
SH
RUN chmod +x /usr/lib/cups/backend/capture

# Entrypoint script: configure & run cupsd, create a printer if missing
RUN bash -lc 'cat >/entrypoint.sh << "EOS"\n\
#!/usr/bin/env bash\n\
set -euo pipefail\n\
: ${PRINTER_NAME:=SimPrinter}\n\
: ${PRINTER_URI:=capture://SimPrinter}\n\
: ${PRINTER_LOCATION:=Lab}\n\
: ${PRINTER_INFO:=Simulated IPP Printer}\n\
: ${ALLOW_WEB:=yes}\n\
: ${RAW_QUEUE:=yes}\n\
mkdir -p /captured /etc/cups/ppd /var/log/cups\n\
# permissive cupsd.conf for lab use (not for prod)\n\
if [ ! -f /etc/cups/cupsd.conf ]; then\n\
  cat >/etc/cups/cupsd.conf <<CONF\n\
LogLevel info\n\
PageLog /var/log/cups/page_log\n\
MaxLogSize 0\n\
Listen 0.0.0.0:631\n\
Browsing On\n\
DefaultAuthType Basic\n\
<Location />\n\
  Order allow,deny\n\
  Allow all\n\
</Location>\n\
<Location /admin>\n\
  Order allow,deny\n\
  Allow all\n\
</Location>\n\
<Location /admin/conf>\n\
  AuthType Basic\n\
  Require user @SYSTEM\n\
  Order allow,deny\n\
  Allow all\n\
</Location>\n\
DefaultEncryption Never\n\
WebInterface On\n\
CONF\n\
fi\n\
/usr/sbin/cupsd -f &\n\
# Wait for CUPS to come up\n\
for i in {1..20}; do curl -sSf http://localhost:631/admin >/dev/null && break || sleep 0.5; done\n\
# Create printer if not exists\n\
if ! lpstat -p \"$PRINTER_NAME\" >/dev/null 2>&1; then\n\
  if [ \"$RAW_QUEUE\" = \"yes\" ]; then\n\
    lpadmin -p \"$PRINTER_NAME\" -E -v \"$PRINTER_URI\" -m raw\n\
  else\n\
    # Example with a Generic PCL driver\n\
    lpadmin -p \"$PRINTER_NAME\" -E -v \"$PRINTER_URI\" -m drv:///sample.drv/generic.ppd\n\
  fi\n\
  lpoptions -p \"$PRINTER_NAME\" -o printer-is-shared=true\n\
  cupsaccept \"$PRINTER_NAME\"\n\
  cupsenable \"$PRINTER_NAME\"\n\
fi\n\
wait -n\n\
EOS\n\
chmod +x /entrypoint.sh'

EXPOSE 631/tcp
VOLUME ["/captured","/etc/cups","/var/log/cups","/var/spool/cups"]
ENTRYPOINT ["/entrypoint.sh"]
