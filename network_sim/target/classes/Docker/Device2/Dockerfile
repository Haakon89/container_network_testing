FROM ubuntu:latest

RUN apt-get update && apt-get install -y \
    curl \
    wget \
    net-tools \
    iputils-ping \
    openssh-server \
    telnet \
    vim \
    nano \
    python3 \
    tcpdump \
    nmap \
    traceroute \
    bind9 \
    apache2 \
    mysql-server \
    && apt-get clean

WORKDIR /home/standard_user
EXPOSE 22
EXPOSE 80
EXPOSE 53
EXPOSE 21
EXPOSE 123
EXPOSE 514
EXPOSE 631
EXPOSE 5353
