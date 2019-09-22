# Contributing

## Response Times

This project is developed and maintained by Dimitris Mandalidis. It was originally
developed by an infrastructure team at Spotify, but it was forked after being declared
mature and further development was frozen. It is being used in a single production-critical 
system where Docker version upgrades are frequent and changes to the API need to
be adapted.

Apart from API adaptations and security fixes (which are a must-have), this is
maintained mostly in my (limited) free time, so I might be slow responding to bugs
and reviewing pull requests. However, if you feel that your bug or pull-request
requires immediate attention and/or an urgent patch please poke me and I 'll do
my best.

## Reporting Bugs

Please make sure you're using the latest version. This project is
released continuously as it's developed so new releases come out almost as frequently as we
commit to master.

## Contributing

Before creating a new issue, see if there's already an existing issue.

If you create a minor bugfix, feel free to submit a PR.
If your PR is for a significant change or a new feature, feel free to ask for our feedback
before writing code to check we're on the same page.

You can build and test by following [instructions here][1].

### Unit tests and integration tests
When adding new functionality to DefaultDockerClient, please consider and
prioritize adding unit tests to cover the new functionality in
[DefaultDockerClientUnitTest][] rather than integration tests that require a
real docker daemon in [DefaultDockerClientTest][].

DefaultDockerClientUnitTest uses a [MockWebServer][] where we can control the
HTTP responses sent by the server and capture the HTTP requests sent by the
DefaultDockerClient, to ensure that it is communicating with the Docker Remote
API as expected.

While integration tests are valuable, they are more brittle and harder to run
than a simple unit test that captures/asserts HTTP requests and responses, and
they end up testing both how docker-client behaves and how the docker daemon
itself behaves.

  [1]: https://github.com/dmandalidis/docker-client#testing
  [DefaultDockerClientTest]: src/test/java/com/spotify/docker/client/DefaultDockerClientTest.java
  [DefaultDockerClientUnitTest]: src/test/java/com/spotify/docker/client/DefaultDockerClientUnitTest.java
  [MockWebServer]: https://github.com/square/okhttp/tree/master/mockwebserver
