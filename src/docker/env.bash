#!/usr/bin/env bash

dockerGet() {
  local url="$1"

  echo -e "GET $(url)/json HTTP/1.0\r\n" | nc -U /var/run/docker.sock
}

APP_PORT_2552_TCP_ADDR=$(dockerGet "/containers/$HOSTNAME")
APP_PORT_2552_TCP_PORT=$(dockerGet "/containers/$HOSTNAME")

BIND_APP_ADDR=$(dockerGet "/containers/$HOSTNAME")
BIND_APP_PORT=$(dockerGet "/containers/$HOSTNAME")

IFS=',' read -ra seed_nodes <<< "$SEED_NODES"
for N in "${!seed_nodes[@]}"; do
  AddJava "-Dakka.actor.cluster.seed-nodes.$N=\"${seed_nodes[N]}\""
done
unset SEED_NODES
