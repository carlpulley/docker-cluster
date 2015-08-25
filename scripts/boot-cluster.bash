#!/bin/bash

set -x
set -e

DRIVER=virtualbox
CONSUL_MASTER="0"
CONSUL_REPLICAS="1"
MASTER="0"
REPLICAS="1"
NODES="1 2"
ALLOW_AGENTS_IN_SWARM_MASTER=false
COMPOSE_FILE=docker-compose.yml
LOGGING=json-file

########################
# Service Discovery and Failover Replicas
########################

PORT=8500
ROLE="service-discovery"

for NAME in $CONSUL_MASTER $CONSUL_REPLICAS; do
  NODE="$ROLE-$NAME"
  
  STATE="$(docker-machine status "$NODE" 2>/dev/null || true)"
  if [ "$STATE" != "" ] && [ "$STATE" != "Running" ]; then
    echo "Removing $NODE node (in $STATE state)..."
    docker-machine stop "$NODE"
    docker-machine rm -f \
        "$NODE"
    echo ""
  fi
  if [ $(docker-machine ls | grep -c "$NODE") -eq 0 ]; then
    echo "Creating $NODE node..."
    docker-machine create \
        -d "$DRIVER" \
        --engine-opt log-driver="$LOGGING" \
        "$NODE"
    echo ""
  fi
  if [ $(docker $(docker-machine config "$NODE") ps | grep -c "$NODE") -eq 0 ] && [ $(docker $(docker-machine config "$NODE") ps -a | grep -c "$NODE") -eq 1 ]; then
    echo "Removing dead $NODE container..."
    docker $(docker-machine config "$NODE") rm \
        "$NODE"
    echo ""
  fi
  if [ $(docker $(docker-machine config "$NODE") ps | grep -c "$NODE") -eq 0 ] && [ $(docker $(docker-machine config "$NODE") ps -a | grep -c "$NODE") -eq 0 ]; then
    echo "Provisioning $NODE node..."
    if [ "$NAME" == "$CONSUL_MASTER" ]; then
      COUNT=$(echo "$CONSUL_MASTER $CONSUL_REPLICAS" | wc -w)
      docker $(docker-machine config "$NODE") run -d \
          -p "$(docker-machine ip "$NODE"):8300:8300" \
          -p "$(docker-machine ip "$NODE"):8301:8301" \
          -p "$(docker-machine ip "$NODE"):8301:8301/udp" \
          -p "$(docker-machine ip "$NODE"):8302:8302" \
          -p "$(docker-machine ip "$NODE"):8302:8302/udp" \
          -p "$(docker-machine ip "$NODE"):8400:8400" \
          -p "$(docker-machine ip "$NODE"):$PORT:$PORT" \
          -h "$NODE" \
          --name "$NODE" \
          --log-driver "$LOGGING" \
          progrium/consul -server -advertise $(docker-machine ip "$NODE") --bootstrap-expect "$COUNT"
    else
      docker $(docker-machine config "$NODE") run -d \
          -p "$(docker-machine ip "$NODE"):8300:8300" \
          -p "$(docker-machine ip "$NODE"):8301:8301" \
          -p "$(docker-machine ip "$NODE"):8301:8301/udp" \
          -p "$(docker-machine ip "$NODE"):8302:8302" \
          -p "$(docker-machine ip "$NODE"):8302:8302/udp" \
          -p "$(docker-machine ip "$NODE"):8400:8400" \
          -p "$(docker-machine ip "$NODE"):$PORT:$PORT" \
          -h "$NODE" \
          --name "$NODE" \
          --log-driver "$LOGGING" \
          progrium/consul -server -advertise $(docker-machine ip "$NODE") --join $(docker-machine ip "$ROLE-$CONSUL_MASTER")
    fi
    echo ""
  fi
done

CONSUL=$(docker-machine ip "$ROLE-$CONSUL_MASTER")
DISCOVERY="consul://$CONSUL:$PORT/services"

########################
# Swarm Master and Failover Replicas
########################

PORT=3376
ROLE="swarm-master"

for NAME in $MASTER $REPLICAS; do
  NODE="$ROLE-$NAME"

  STATE=$(docker-machine status "$NODE" 2>/dev/null || true)
  if [ "$STATE" != "" ] && [ "$STATE" != "Running" ]; then
    echo "Removing $NODE node (in $STATE state)..."
    docker-machine rm -f \
        "$NODE"
    echo ""
  fi
  if [ $(docker-machine ls | grep -c "$NODE") -eq 0 ]; then
    echo "Creating $NODE node..."
    docker-machine create \
        -d "$DRIVER" \
        --swarm \
        --swarm-master \
        --swarm-discovery "$DISCOVERY" \
        --engine-opt log-driver="$LOGGING" \
        "$NODE"
    echo ""
  fi
done

########################
# Swarm Agents
########################

PORT=2376
ROLE="swarm-agent"

for NAME in $NODES; do
  NODE="$ROLE-$NAME"

  STATE=$(docker-machine status "$NODE" 2>/dev/null || true)
  if [ "$STATE" != "" ] && [ "$STATE" != "Running" ]; then
    echo "Removing $NODE node (in $STATE state)..."
    docker-machine rm -f \
        "$NODE"
    echo ""
  fi
  if [ $(docker-machine ls | grep -c "$NODE") -eq 0 ]; then
    echo "Creating $NODE node..."
    docker-machine create \
        -d "$DRIVER" \
        --swarm \
        --swarm-discovery "$DISCOVERY" \
        --engine-opt log-driver="$LOGGING" \
        "$NODE"
    echo ""
  fi
done

eval "$(docker-machine env --swarm "swarm-$MASTER")"
echo ""

#docker-compose -f $COMPOSE_FILE up -d
#echo ""

docker-machine ls
echo ""
docker $(docker-machine config --swarm "swarm-$MASTER") ps -a
echo ""
echo "Successfully booted the Docker Swarm cluster (backed by Consul service discovery)"
echo ""
echo "  - To view running services: 'docker-compose ps'"
echo "  - To scale a service: 'docker-compose scale \$SERVICE=\$N'"
echo "  - To stop all running services: 'docker-compose stop \$SERVICE'"
echo "  - To remove all stopped services: 'docker-compose rm \$SERVICE'"
echo "  - To view running containers: 'docker ps'"
echo "  - To stop a container: 'docker stop \$CONTAINER'"
echo "  - To remove a stopped container: 'docker rm \$CONTAINER'"
