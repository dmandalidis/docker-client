# Changelog

If you are interested in the history of changes when this project was managed
by Spotify you can refer to [Changelog pre-fork history][]

[Changelog pre-fork history]: https://github.com/dmandalidis/docker-client/blob/master/CHANGELOG_PREFORK.md

## 1.0.0

This is the first release after forking the project. The only notable changes are 
the switch from `com.spotify` to `org.mandas` groupId and [the upgrade to Jackson 2.9.10][].
Users of the original project should be able to switch by simply replacing the groupId
and the version (otherwise it's a bug, triggering an immediate patch release)

[the upgrade to Jackson 2.9.10]: https://github.com/dmandalidis/docker-client/issues/3