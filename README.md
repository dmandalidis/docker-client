# Docker Client

[![Build Status](https://travis-ci.org/dmandalidis/docker-client.svg?branch=master)](https://travis-ci.org/dmandalidis/docker-client)
[![codecov](https://codecov.io/github/dmandalidis/docker-client/coverage.svg?branch=master)](https://codecov.io/github/dmandalidis/docker-client?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/org.mandas/docker-client.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.mandas%22%20docker-client)
[![License](https://img.shields.io/github/license/dmandalidis/docker-client.svg)](LICENSE)

This is a [Docker](https://github.com/docker/docker) client written in Java.
It was used in many critical production systems at Spotify until its 
[fork](https://github.com/dmandalidis/docker-client/blob/master/FORK.md) on September 2019.

* [Version compatibility](#version-compatibility)
* [Download](#download)
* [Usage Example](#usage-example)
* [Getting Started](#getting-started)
* [Prerequisites](#prerequisites)
* [Testing](#testing)
* [Releasing](#releasing)
* [A Note on Shading](#a-note-on-shading)
* [User Manual](https://github.com/dmandalidis/docker-client/blob/master/docs/user_manual.md)

## Version compatibility
docker-client is built and tested against the most recent releases of `docker-ce` (actually 
those that can be found in Ubuntu xenial). The plan is to eventually align with the 
respective Docker support cycle for each version (7 months).

This doesn't mean that we will break everything on every `docker-ce` release, but bugs 
that cannot be reproduced using the supported docker versions will not be fixed. 

For a list the currently supported docker versions you can take a look [here][1], while the API
version compatibility matrix can be found at the *API version matrix* section on 
[Docker docs on the mapping between Docker version and API version][3].

The artifacts tested against the latest supported Docker version will be the ones
uploaded to maven central.

## Download

Download the latest JAR or grab [via Maven][maven-search].

```xml
<dependency>
  <groupId>org.mandas</groupId>
  <artifactId>docker-client</artifactId>
  <version>LATEST-VERSION</version>
</dependency>
```


## Usage Example

```java
// Create a client based on DOCKER_HOST and DOCKER_CERT_PATH env vars
final DockerClient docker = DefaultDockerClient.fromEnv().build();

// Pull an image
docker.pull("busybox");

// Bind container ports to host ports
final String[] ports = {"80", "22"};
final Map<String, List<PortBinding>> portBindings = new HashMap<>();
for (String port : ports) {
    List<PortBinding> hostPorts = new ArrayList<>();
    hostPorts.add(PortBinding.of("0.0.0.0", port));
    portBindings.put(port, hostPorts);
}

// Bind container port 443 to an automatically allocated available host port.
List<PortBinding> randomPort = new ArrayList<>();
randomPort.add(PortBinding.randomPort("0.0.0.0"));
portBindings.put("443", randomPort);

final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

// Create container with exposed ports
final ContainerConfig containerConfig = ContainerConfig.builder()
    .hostConfig(hostConfig)
    .image("busybox").exposedPorts(ports)
    .cmd("sh", "-c", "while :; do sleep 1; done")
    .build();

final ContainerCreation creation = docker.createContainer(containerConfig);
final String id = creation.id();

// Inspect container
final ContainerInfo info = docker.inspectContainer(id);

// Start container
docker.startContainer(id);

// Exec command inside running container with attached STDOUT and STDERR
final String[] command = {"sh", "-c", "ls"};
final ExecCreation execCreation = docker.execCreate(
    id, command, DockerClient.ExecCreateParam.attachStdout(),
    DockerClient.ExecCreateParam.attachStderr());
final LogStream output = docker.execStart(execCreation.id());
final String execOutput = output.readFully();

// Kill container
docker.killContainer(id);

// Remove container
docker.removeContainer(id);

// Close the docker client
docker.close();
```

## Getting Started

If you're looking for how to use docker-client, see the [User Manual][2].
If you're looking for how to build and develop it, keep reading.

## Prerequisites

docker-client should be buildable on any platform with Docker 1.6+, JDK8+, and a recent version of
Maven 3.

### A note on using Docker for Mac

If you are using Docker for Mac and `DefaultDockerClient.fromEnv()`, it might not be clear
what value to use for the `DOCKER_HOST` environment variable. The value you should use is
`DOCKER_HOST=unix:///var/run/docker.sock`, at least as of version 1.11.1-beta11.

As of version 4.0.8 of docker-client, `DefaultDockerClient.fromEnv()` uses
`unix:///var/run/docker.sock` on OS X by default.

## Testing

If you're running a recent version of docker (>= 1.12), which contains native swarm support, please
ensure that you run `docker swarm init` to initialize the docker swarm.

Make sure Docker daemon is running and that you can do `docker ps`.

You can run tests on their own with `mvn test`. Note that the tests start and stop a large number of
containers, so the list of containers you see with `docker ps -a` will start to get pretty long
after many test runs. You may find it helpful to occasionally issue `docker rm $(docker ps -aq)`.

## Releasing

Commits to the master branch will trigger our continuous integration agent to build the jar and
release by uploading to Sonatype. If you are a project maintainer with the necessary credentials,
you can also build and release locally by running the below.

```sh
mvn clean [-DskipTests -Darguments=-DskipTests] -Dgpg.keyname=<key ID used for signing artifacts> release:prepare release:perform
```

## A note on shading

Please note that there is also a shaded variant.

Standard:

```xml
<dependency>
  <groupId>org.mandas</groupId>
  <artifactId>docker-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

Shaded:

```xml
<dependency>
  <groupId>org.mandas</groupId>
  <artifactId>docker-client</artifactId>
  <classifier>shaded</classifier>
  <version>1.0.0</version>
</dependency>
```

**This is particularly important if you use Jersey 1.x in your project. To avoid conflicts with
docker-client and Jersey 2.x, you will need to explicitly specify the shaded version above.**


  [1]: https://travis-ci.org/dmandalidis/docker-client
  [2]: docs/user_manual.md
  [3]: https://docs.docker.com/develop/sdk/


[maven-search]: https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.mandas%22%20docker-client
