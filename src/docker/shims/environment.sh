#!/usr/bin/env bash

consulGet() {
  local url="$1"
  local path="$2"

  curl -q http://$(CONSUL)/v1/kv/$(url)?keys&separator="/" | jq "$(path)"
}

getAppPort2552TcpAddr() {
  consulGet "???" '???' # TODO:
}

getAppPort2552TcpPort() {
  echo -e "GET /containers/$HOSTNAME/json HTTP/1.0\r\n" | nc -U /var/run/docker.sock | sed '1,/^\r$/d' | jq '.NetworkSettings.Ports["2552/tcp"][].HostPort'
}

getSeedNodes() {
  consulGet "???" '???' # TODO:
}

APP_PORT_2552_TCP_ADDR=$(getAppPort2552TcpAddr)
APP_PORT_2552_TCP_PORT=$(getAppPort2552TcpPort)

IFS=',' read -ra seed_nodes <<< "$(getSeedNodes)"
for N in "${!seed_nodes[@]}"; do
  AddJava "-Dakka.actor.cluster.seed-nodes.$N=\"${seed_nodes[N]}\""
done
