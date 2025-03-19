# Change Log

## 9.0.2 - notable changes

* Exposed non-sanitized URI (#861)
* Fix builder weird state management for registry auth (#861)

## 9.0.1 - notable changes

* Bump com.fasterxml.jackson:jackson-bom from 2.18.2 to 2.18.3
* Bump org.slf4j:slf4j-api from 2.0.16 to 2.0.17
* Bump ch.qos.logback:logback-classic from 1.5.14 to 1.5.17
* Drop obsolete jackson-jaxrs-base (fixes #852)
* Bump org.bouncycastle:bcpkix-jdk18on from 1.79 to 1.80
* Bump version.jersey from 3.1.9 to 3.1.10

## 9.0.0 - notable changes

* **breaking** RESTeasy support (and subsequently custom HTTP client support) was dropped.
    * It generated high maintenance complexity while the project's evolution was held back
to achieve JAX-RS version compatibility
* **breaking** Based on the above, building a `DockerClient` is heavily [simplified](./README.md)
    * manually pulling Jersey dependencies is no longer needed
* **breaking** Google OAuth2 support was dropped
    * Users are advised to copy `ContainerRegistryAuthSupplier` from a `docker-client` 8.x version
    and maintain it on their own side.

## 8.0.3 - notable changes

* Bump ch.qos.logback:logback-classic from 1.5.10 to 1.5.11
* Bump com.github.jnr:jnr-unixsocket from 0.38.22 to 0.38.23
* Bump ch.qos.logback:logback-classic from 1.5.9 to 1.5.10
* Bump version.jersey from 3.1.8 to 3.1.9
* Bump ch.qos.logback:logback-classic from 1.5.8 to 1.5.9
* Bump com.fasterxml.jackson:jackson-bom from 2.17.2 to 2.18.0
* Bump ch.qos.logback:logback-classic from 1.5.7 to 1.5.8
* Fix v1.46 deprecations (fixes #791)
* Bump org.apache.commons:commons-compress from 1.27.0 to 1.27.1

## 8.0.2 - notable changes

* Downgrade OSGi-incompatible Jakarta REST API (fixes #773)
* Bump com.google.auth:google-auth-library-oauth2-http
* Bump ch.qos.logback:logback-classic from 1.5.6 to 1.5.7
* Bump org.apache.commons:commons-compress from 1.26.2 to 1.27.0
* Bump org.slf4j:slf4j-api from 2.0.13 to 2.0.16

## 8.0.1 - notable changes

* make userNs configurable
* Bump version.jersey from 3.1.7 to 3.1.8

## 8.0.0 - notable changes

* Update to Java 17
* Bump version.jersey from 3.1.5 to 3.1.7
* Bump jakarta.ws.rs:jakarta.ws.rs-api from 3.1.0 to 4.0.0
* @nullable for driver and count
* Add @nullable to optional fields in DeviceRequest
* Bump com.fasterxml.jackson:jackson-bom from 2.16.1 to 2.17.2
* Drop deprecated v1.41 fields
* Bump org.apache.commons:commons-compress from 1.25.0 to 1.26.2
* Bump ch.qos.logback:logback-classic from 1.4.14 to 1.5.6
* Bump com.github.jnr:jnr-unixsocket from 0.38.21 to 0.38.22
* Handle network conflict creation
* Add deprecations
* Deprecate MacAddress
* Deprecate virtualSize from Image
* Bump org.slf4j:slf4j-api from 2.0.10 to 2.0.13
* Switch to org.bouncycastle:bcpkix-jdk18on

## 7.0.8 - notable changes

* Bump org.apache.commons:commons-compress from 1.25.0 to 1.26.0

## 7.0.7 - notable changes

* Add deprecations
* Deprecate MacAddress
* Deprecate virtualSize from Image
* Fix ImageInfo message for Docker 25.0
* Bump org.slf4j:slf4j-api from 2.0.10 to 2.0.11

## 7.0.6 - notable changes

* Switch to org.bouncycastle:bcpkix-jdk18on

## 7.0.5 - notable changes

* Bump com.fasterxml.jackson:jackson-bom from 2.16.0 to 2.16.1
* Bump org.slf4j:slf4j-api from 2.0.9 to 2.0.10
* Bump version.jersey from 3.1.4 to 3.1.5
* Bump ch.qos.logback:logback-classic from 1.4.13 to 1.4.14
* Bump version.jersey from 3.1.3 to 3.1.4

## 7.0.4 - notable changes

* Bump ch.qos.logback:logback-classic from 1.4.7 to 1.4.13
* Bump com.fasterxml.jackson:jackson-bom from 2.15.2 to 2.16.0
* Bump org.immutables:value from 2.9.3 to 2.10.0
* Bump com.github.jnr:jnr-unixsocket from 0.38.20 to 0.38.21
* Bump org.apache.commons:commons-compress from 1.23.0 to 1.25.0
* Bump org.slf4j:slf4j-api from 2.0.7 to 2.0.9
* Bump version.jersey from 3.1.2 to 3.1.3
* Bump jimfs from 1.2 to 1.3.0
* Bump google-auth-library-oauth2-http from 1.17.0 to 1.20.0
* Fix unclosed connections
* Drop internal no-timeout client

## 7.0.3 - notable changes

* Ignore proxy configuration when non-http(s) URI is used (fixes #601)
* Bump jackson-bom from 2.15.1 to 2.15.2
* Bump jnr-unixsocket from 0.38.19 to 0.38.20
* Bump google-auth-library-oauth2-http from 1.16.1 to 1.17.0

## 7.0.2 - notable changes

* Bump version.jersey from 3.1.1 to 3.1.2
* Bump jackson-bom from 2.14.2 to 2.15.1
* Bump google-auth-library-oauth2-http from 1.16.0 to 1.16.1
* Bump commons-compress from 1.22 to 1.23.0
* Bump slf4j-api from 2.0.6 to 2.0.7
* Bump logback-classic from 1.4.5 to 1.4.7

## 7.0.1 - notable changes

* Propagate Docker error response body (fixes #558)
* Bump version.jersey from 3.1.0 to 3.1.1
* Bump google-auth-library-oauth2-http from 1.14.0 to 1.16.0
* Bump jackson-bom from 2.14.1 to 2.14.2

## 7.0.0 - notable changes

* Update to Java 11
* Bump slf4j-api from 2.0.4 to 2.0.6
* Bump google-auth-library-oauth2-http from 1.13.0 to 1.14.0

## 6.1.0 - notable changes

* Type error in DeviceRequest#capabilities (fixes #531)
* Bump jnr-unixsocket from 0.38.17 to 0.38.19
* Bump commons-compress from 1.21 to 1.22
* Update JAXRS to 3.1.0
* Update Jersey to 3.1.0
* Bump logback-classic from 1.4.0 to 1.4.5
* Bump slf4j-api from 2.0.0 to 2.0.4
* Bump google-auth-library-oauth2-http from 1.10.0 to 1.13.0
* Bump jackson-bom from 2.13.3 to 2.14.1
* Drop commons-codec dependency

## 6.0.5 - notable changes

* Explicitly pull commons-codec (fixes #493)
* Bump logback-classic from 1.2.11 to 1.4.0
* Bump slf4j-api from 1.7.36 to 2.0.0
* Bump google-auth-library-oauth2-http from 1.8.0 to 1.10.0
* Bump version.resteasy from 6.0.1.Final to 6.0.3.Final

## 6.0.4 - notable changes

* Bump version.resteasy from 6.0.0.Final to 6.0.1.Final
* Bump jackson-bom from 2.13.1 to 2.13.3
* Bump google-auth-library-oauth2-http from 1.5.3 to 1.8.0
* Bump logback-classic from 1.2.10 to 1.2.11

## 6.0.3 - notable changes

* Bump google-auth-library-oauth2-http from 1.4.0 to 1.5.3
* Bump value from 2.8.8 to 2.9.0

## 6.0.2 - notable changes

* Workaround NEXUS-31214

## 6.0.1 - notable changes

* Fix JAX-RS API dependency (fixes #432)
* Bump slf4j-api from 1.7.33 to 1.7.36
* Bump google-auth-library-oauth2-http from 1.3.0 to 1.4.0

## 6.0.0 - notable changes

* Bump slf4j-api from 1.7.32 to 1.7.33
* Bump jnr-unixsocket from 0.38.15 to 0.38.17
* Upgrade to JAX-RS 3 (fixes #408)
    * (breaking change) RESTeasy (if used) must be updated to at least 6.0.0.Final
    * (breaking change) Jersey (if used) must be updated to at least 3.0.0


## 5.2.2 - notable changes

* Fix missing builder (#404)
* Bump jackson-bom from 2.13.0 to 2.13.1

## 5.2.1 - notable changes

* Downgrade JAXRS (fixes #391)
* Bump jnr-unixsocket from 0.38.13 to 0.38.15
* Bump version.resteasy from 5.0.0.Final to 5.0.1.Final
* Bump bcpkix-jdk15on from 1.69 to 1.70

## 5.2.0 - notable changes

* Bump jnr-unixsocket from 0.38.8 to 0.38.13
* Bump logback-classic from 1.2.5 to 1.2.7
* Bump google-auth-library-oauth2-http from 1.0.0 to 1.3.0
* Bump version.resteasy from 4.7.1.Final to 5.0.0.Final
* Bump jackson-bom from 2.12.4 to 2.13.0
* Bump version.jersey from 2.34 to 2.35
* Support OSVersion (fixes #346)
* Support CgroupVersion (fixes #345)
* Mark v1.41 deprecations (fixes #344)
* Support job status (fixes #343)

## 5.1.0 - notable changes

* Support for swarm jobs (fixes #341)

## 5.0.2 - notable changes

* Bump version.resteasy from 4.6.0.Final to 4.7.1.Final
* Bump google-auth-library-oauth2-http from 0.25.2 to 1.0.0
* Bump logback-classic from 1.2.3 to 1.2.5
* Bump slf4j-api from 1.7.30 to 1.7.32
* Bump commons-compress from 1.19 to 1.21
* Bump jackson-bom from 2.12.2 to 2.12.4
* Bump bcpkix-jdk15on from 1.68 to 1.69
* Bump jnr-unixsocket from 0.38.6 to 0.38.8
* Bump version.jersey from 2.32 to 2.34

## 5.0.1

* Bump google-auth-library-oauth2-http from 0.22.2 to 0.25.2
* Bump jnr-unixsocket from 0.38.5 to 0.38.6
* Bump jackson-bom from 2.12.0 to 2.12.2
* Bump version.resteasy from 4.5.8.Final to 4.6.0.Final

## 5.0.0

* Drop full list of capabilities
* HostConfig#kernelMemory deprecated in 20.10.0
* Bump google-auth-library-oauth2-http from 0.22.1 to 0.22.2
* Bump jnr-unixsocket from 0.38.3 to 0.38.5
* Bump bcpkix-jdk15on from 1.67 to 1.68
* Bump jakarta.ws.rs-api from 2.1.6 to 3.0.0
* Bump google-auth-library-oauth2-http from 0.22.0 to 0.22.1
* Bump jackson-bom from 2.11.3 to 2.12.0

## 4.0.3

* Bump bcpkix-jdk15on from 1.66 to 1.67
* Bump google-auth-library-oauth2-http from 0.21.1 to 0.22.0
* Bump jackson-bom from 2.11.2 to 2.11.3
* Bump version.jersey from 2.31 to 2.32
* Bump version.resteasy from 4.5.6.Final to 4.5.8.Final
* Bump jnr-unixsocket from 0.34 to 0.38.3

## 4.0.2

* Selective blocking I/O calls and handle missing HTTP statuses (fixes #229)(fixes #231)
* Bump jnr-unixsocket from 0.33 to 0.34

## 4.0.1

* Bump version.jersey from 2.30.1 to 2.31
* Generate proper MANIFEST.MF
* Bump jackson-bom from 2.11.1 to 2.11.2
* Bump version.resteasy from 4.5.5.Final to 4.5.6.Final
* Bump jnr-unixsocket from 0.32 to 0.33
* Bump bcpkix-jdk15on from 1.65 to 1.66
* Bump google-auth-library-oauth2-http from 0.21.0 to 0.21.1

## 4.0.0

### Breaking changes

* `ResourceRequirements#reservations` changed type from `Resources` to the newly-introduced
`Reservations`. The reasoning behind this change is that the `Resources` type cannot
hold the necessary data for supporting swarm generic resources.

* Support for providing any JAX-RS 2.1 client implementation has been added to avoid 
conflicts in flat classpath environments where another JAX-RS implementations (e.g. RESTeasy)
are unavoidably present or where an externally-managed `Client` needs to be shared. This led
all Jersey dependencies to become optional and clients projects must define them explicitly
if they need to use the bundled `JerseyDockerClientBuilder`. More specifically, client 
projects are affected in the following ways:
    * They must include the necessary Jersey dependencies in their `pom.xml` as follows
    ```xml
        <dependency>
            <groupId>org.glassfish.jersey.core</groupId>
            <artifactId>jersey-client</artifactId>
            <version>2.30.1</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jersey.inject</groupId>
            <artifactId>jersey-hk2</artifactId>
            <version>2.30.1</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jersey.connectors</groupId>
            <artifactId>jersey-apache-connector</artifactId>
            <version>2.30.1</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jersey.media</groupId>
            <artifactId>jersey-media-json-jackson</artifactId>
            <version>2.30.1</version>
        </dependency>
    ```
    * Public constructors of `DefaultDockerClient` have been dropped in favour of clear 
    responsibilities between the builder and the client itself:
        * If you were using `DefaultDockerClient#DefaultDockerClient(URI)` use `new JerseyDockerClientBuilder().uri(uri).build()` instead
        * If you were using `DefaultDockerClient#DefaultDockerClient(String)` use `new JerseyDockerClientBuilder().uri(uri).build()` instead
        * If you were using `DefaultDockerClient#DefaultDockerClient(URI, DockerCertificatesStore)` use `new JerseyDockerClientBuilder().uri(uri).dockerCertificates(dockerCertificatesStore).build()` instead
    * `DefaultDockerClient#fromEnv` has been moved to `JerseyDockerClientBuilder`
        * If you were using `DefaultDockerClient#fromEnv` use `new JerseyDockerClientBuilder().fromEnv` instead

    * `DefaultDockerClient#builder` has been removed
        * If you were using `DefaultDockerClient#builder` use `new JerseyDockerClientBuilder()` instead

    * `DockerClient#events` no longer throws a `DockerException` wrapping an `IOException`

    * Builder `requestEntityProcessing` has changed to use a new enum shipped by the DockerClient and renamed to `entityProcessing`

    * Selective per-request unlimited read timeout has been dropped. The reason for this is that there's no standard way to 
    do it and we didn't want to create a new `Client` in such methods

    * `InterruptedIOException` is now always wrapped to `DockerTimeoutException`

* Shading support has been dropped as a side-effect of providing API consistency between
different client builders.

* `DockerRequestException` has stopped holding the response body at all cases. This 
is because RESTeasy (compared to Jersey) closes the response when an exception is thrown, 
hiding the actual error when trying to read it. The specification is still 
[unclear](https://github.com/eclipse-ee4j/jaxrs-api/issues/736) about whether JAXRS 
implementations must close the response or not.

### Changes

* Avoid reading response entity on exceptions
* Bump jackson-bom from 2.10.2 to 2.11.1
* [documentation] appendBinds and TOC fix (fixes #192)
* Bump google-auth-library-oauth2-http from 0.20.0 to 0.21.0
* Drop async() invocations
* Bump jnr-unixsocket from 0.25 to 0.32
* README TOC fix (fixes #170)
* Rationalize DefaultDockerClient and Builder responsibities (fixes #161, #91)
* Drop commons-lang dependency
* HostConfig.toBuilder fix to use existing values (fixes #166)
* Bump bcpkix-jdk15on from 1.64 to 1.65
* Swarm support for generic resources (fixes #155)
* Bump version.jersey from 2.30 to 2.30.1
* Auto-close json generator
* Remove commons-io and javax.activation dependencies

## 3.2.1

* HostConfig.toBuilder fix to use existing values (fixes #166)(thanks [@ko-mueller](https://github.com/ko-mueller))
* Bump bcpkix-jdk15on from 1.64 to 1.65
* Bump jackson-bom from 2.10.2 to 2.10.3
* Bump version.jersey from 2.30 to 2.30.1
* Auto-close json generator
* Remove commons-io and javax.activation dependencies
* Bump jnr-unixsocket from 0.25 to 0.28

## 3.2.0

* Bump google-auth-library-oauth2-http from 0.18.0 to 0.20.0
* Bump version.jersey from 2.29.1 to 2.30
* Bump jnr-unixsocket from 0.24 to 0.25
* Bump jackson-bom from 2.10.1 to 2.10.2
* Support device requests (fixes #75)
* Bump slf4j-api from 1.7.29 to 1.7.30
* Fix javadoc reporting

## 3.1.0

* Support secret list filtering (fixes #120)
* Support capabilities in HostConfig (fixes #105)
* Support max replicas per node (fixes #103)
* Support filtering dangling networks (fixes #104)
* Support sysctl for services (fixes #101)
* Support config events (fixes #108)

## 3.0.1

* Revert incorrectly removed methods (fixes #111)

## 3.0.0

This release is mostly a Guava-exclusion release since several of its used features
has been incorporated to Java 8+.

Additionally, although not expected to cause any issues, the `com.fasterxml.jackson.module:jackson-module-jaxb-annotations`
has been excluded and thus stopped being pulled as a transitive dependency of `docker-client`.

* Create shared connection manager
* Remove workaround for JERSEY-2698
* Update Jersey and http-client to latest versions (fixes #45)
* Exclude jaxb annotations (fixes #96)
* Drop guava (fixes #74)
* Add HostConfig.Sysctls support (fixes #86)
* Bump jnr-unixsocket from 0.23 to 0.24
* Replace autovalue with immutables

### Breaking changes

* `RegistryConfigs.Builder#addConfig` does not ignore null `RegistryAuth` values anymore
* `HostConfig.Builder#appendBinds*` methods removed
* `HostConfig.Builder#binds(final Bind... binds)` method does not ignore null binds anymore
* `Immutable*` types removed from public fields
* `EventStream` now implements `java.util.Iterator<Event>`
* `EventStream#close` and `LogStream#close` can now throw `IOException`
* `LogStream#attach(OutputStream, OutputStream, boolean)` method removed. Methods should
not close streams they do not manage.
* `LogStream#attach(OutputStream, OutputStream)` can now throw `IOException`

## 2.0.2

* Proper cross-compile configuration (fixes #88)(thanks [@ieggel](https://github.com/ieggel))

## 2.0.1

* Add the correct groupId for shading javax.ws.rs (fixes #84)(thanks [@ieggel](https://github.com/ieggel))
* Bump version.jackson from 2.10.0 to 2.10.1
* Bump slf4j-api from 1.7.28 to 1.7.29
* Bump joda-time from 2.10.4 to 2.10.5
* Bump bcpkix-jdk15on from 1.63 to 1.64

## 2.0.0

This release is the last step in moving away from the original project. That is,
this is a breaking release as all `com.spotify.docker` packages have been renamed
to `org.mandas.docker` and, normally, A find-and-replace would do the work for you.
Last but not least, all deprecated methods and redundant API version checks have
been removed.

* Repackaging to org.mandas (fixes #8)
* Remove deprecated code

## 1.1.1

* Update commons-compress (CVE-2019-12402)

## 1.1.0

### Enhancements

* Support Init for containers and services (fixes [#36](https://github.com/dmandalidis/docker-client/issues/36))
* Support --mount option in container creation (fixes [#59](https://github.com/dmandalidis/docker-client/issues/10))
* Fix [#44](https://github.com/dmandalidis/docker-client/issues/44) Add missing 'Order' attribute in UpdateConfig (thanks [@Allen57](https://github.com/Allen57))

### Misc

* Build against docker-ce 19.03.3
* Bump google-auth-library-oauth2-http from 0.17.2 to 0.18.0
* Bump version.jackson from 2.9.10 to 2.10.0
* Bump activation from 1.1 to 1.1.1
* Bump bcpkix-jdk15on from 1.60 to 1.63
* Bump mockwebserver from 4.2.1 to 4.2.2
* Bump slf4j-api from 1.7.22 to 1.7.28
* Bump spotbugs-maven-plugin from 3.1.12 to 3.1.12.2
* Bump guava from 24.1.1-jre to 28.1-jre
* Support the most recent docker versions
* Fix in README link
* Switch to ubuntu xenial and OpenJDK11 (fixes #5, #7)
* Caching maven artifacts

## 1.0.2

A maintenance release comprised mostly by library updates. It must not break anyone, but
if your project was:

* falsely using a transitive dependency from this project
* excluding transitive dependencies from this project

you may need to revisit your `pom.xml`. Here are the notable changes:

* Update Guava (fixes [#10](https://github.com/dmandalidis/docker-client/issues/10))
* Update jersey
* Bump maven-compiler-plugin from 3.8.0 to 3.8.1
* Bump jacoco-maven-plugin from 0.8.2 to 0.8.4
* Bump commons-io from 2.5 to 2.6
* Bump awaitility from 2.0.0 to 4.0.1
* Bump jsr305 from 3.0.1 to 3.0.2
* Bump google-auth-library-oauth2-http from 0.6.0 to 0.17.2
* Bump mockwebserver from 3.8.0 to 4.2.1
* Bump maven-project-info-reports-plugin from 2.8 to 3.0.0
* Bump jnr-unixsocket from 0.18 to 0.23
* Bump jimfs from 1.0 to 1.1
* Bump hamcrest-pojo from 1.1.3 to 1.1.5
* Bump logback-classic from 1.2.1 to 1.2.3
* Bump hamcrest-jackson from 1.1.3 to 1.1.5
* Lint javadoc comments (fixes [#14](https://github.com/dmandalidis/docker-client/issues/14))
* Bump mockito-core from 1.10.19 to 3.1.0
* Bump httpcore from 4.4.5 to 4.4.12
* Bump maven-source-plugin from 2.4 to 3.1.0
* Bump maven-shade-plugin from 3.1.1 to 3.2.1
* Bump joda-time from 2.8.2 to 2.10.4

## 1.0.1

This is the first release after forking the project. The only notable changes are 
the switch from `com.spotify` to `org.mandas` groupId and [the upgrade to Jackson 2.9.10][].
Users of the original project should be able to switch by simply replacing the groupId
and the version (otherwise it's a bug, triggering an immediate patch release)

[the upgrade to Jackson 2.9.10]: https://github.com/dmandalidis/docker-client/issues/3

## Previous releases 

If you are interested in the history of changes when this project was managed
by Spotify you can refer to [Changelog pre-fork history][]

[Changelog pre-fork history]: https://github.com/dmandalidis/docker-client/blob/main/CHANGELOG_PREFORK.md