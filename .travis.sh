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

    sudo mv /var/lib/dpkg/info/docker-ce* /tmp
    sudo apt-get -q -y purge docker-ce docker-ce-cli containerd.io
    sudo apt-get remove docker docker-engine docker.io containerd runc
    sudo apt-get -qq update
    PACKAGE_VERSION=$(sudo apt-cache madison docker-ce | grep $DOCKER_VERSION | cut -f2 -d"|" | tr -d '[:space:]')
    if [[ -z $PACKAGE_VERSION ]]; then
      echo "No candidate package with $DOCKER_VERSION was found";
      exit 1;
    fi

    sudo apt-get -q -y -o Dpkg::Options::="--force-confnew" install docker-ce=$PACKAGE_VERSION
    sudo journalctl -xe
    sudo docker info
    sudo docker swarm init --advertise-addr 127.0.0.1
    sudo echo ${DOCKER_PASSWORD} | docker login --username ${DOCKER_USER} --password-stdin
    ;;

  dump_docker_config)
    echo "Contents of /etc/docker/daemon.json:"
    sudo cat /etc/docker/daemon.json || :

    echo "Contents of $HOME/.dockercfg:"
    cat $HOME/.dockercfg || :

    echo "Service logs"
    sudo systemctl -l status docker --no-pager

    ;;

  *)
    echo "Unknown command $1"
    exit 2
esac
