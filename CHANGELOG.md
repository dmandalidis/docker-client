# Changelog

If you are interested in the history of changes when this project was managed
by Spotify you can refer to [Changelog pre-fork history][]

[Changelog pre-fork history]: https://github.com/dmandalidis/docker-client/blob/master/CHANGELOG_PREFORK.md

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