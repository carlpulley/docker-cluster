#!/usr/bin/env bash

consulGet() {
  local url="$1"
  local path="$2"

  curl -q http://$(CONSUL)/v1/kv/$(url)?keys&separator="/" | jq "$(path)"
}

localDockerGet() {
  local url="$1"
  local path="$2"

  echo -e "GET $(url) HTTP/1.0\r\n" | nc -U /var/run/docker.sock | sed '1,/^\r$/d' | jq "$(path)"
}

APP_PORT_2552_TCP_ADDR=$(consulGet "???" '???') # TODO:
APP_PORT_2552_TCP_PORT=$(localDockerGet "/containers/$HOSTNAME/json" '.NetworkSettings.Ports["2552/tcp"][].HostPort')

IFS=',' read -ra seed_nodes <<< "$(consulGet "???" '???')" # TODO:
for N in "${!seed_nodes[@]}"; do
  AddJava "-Dakka.actor.cluster.seed-nodes.$N=\"${seed_nodes[N]}\""
done
