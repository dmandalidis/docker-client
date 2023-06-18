#!/bin/bash
set -e

if [[ -z $1 ]]; then
  "I need a command!"
  exit 1
fi

case "$1" in
  install_docker)

    if [[ -z $DOCKER_VERSION ]]; then
      echo "DOCKER_VERSION needs to be set as an environment variable"
      exit 1
    fi

    # TODO detect which docker version is already installed and skip
    # uninstall/reinstall if it matches $DOCKER_VERSION

    sudo systemctl stop docker || :
    sudo rm -fr /var/lib/docker || :

    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

    sudo apt-get -qq update
    sudo apt-get -qq remove --purge docker.io docker-doc docker-compose podman-docker containerd runc
    sudo mkdir -p /etc/systemd/system/docker.service.d/
    sudo cp .github/files/exec-override.conf /etc/systemd/system/docker.service.d/exec-override.conf
    sudo systemctl daemon-reload
    sudo rm -rf /etc/docker
    sudo rm -rf /etc/default/docker
    PACKAGE_VERSION=$(sudo apt-cache madison docker-ce | grep $DOCKER_VERSION | cut -f2 -d"|" | tr -d '[:space:]')
    if [[ -z $PACKAGE_VERSION ]]; then
      echo "No candidate package with $DOCKER_VERSION was found";
      exit 1;
    fi

    sudo apt-get -q -y install docker-ce=$PACKAGE_VERSION
    sudo docker info
    sudo docker swarm init --advertise-addr 127.0.0.1
    sudo echo ${DOCKER_PASSWORD} | docker login --username ${DOCKER_USER} --password-stdin
    ;;

  dump_docker_config)
    echo "Contents of /etc/docker/daemon.json:"
    sudo cat /etc/docker/daemon.json || :

    echo "Service status"
    sudo systemctl -l status docker --no-pager

    echo "Service logs"
    sudo journalctl -u docker --no-pager

    ;;
  *)
    echo "Unknown command $1"
    exit 2
esac
