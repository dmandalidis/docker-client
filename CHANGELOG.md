# Change Log

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

[Changelog pre-fork history]: https://github.com/dmandalidis/docker-client/blob/master/CHANGELOG_PREFORK.md