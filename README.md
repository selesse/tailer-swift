tailer-swift
============

tailer-swift is a cross-platform GUI specialization of the popular tail
utility. Highlights include easy highlighting, filtering and
regular-expression support.

Installation
------------

1. Make sure Java 7+ is installed.
2. Clone this repository.
3. Run the Gradle wrapper. On Windows, `gradlew.bat`. On anything else,
   run `gradlew`. The Gradle wrapper will install Gradle; Gradle will then
   install all the dependencies.

Requirements
------------

Java 7+ needs to be installed. Everything else will be handled by the Gradle
wrapper. The following libraries are used:

  * [Guava](https://code.google.com/p/guava-libraries/)
  * [SLF4J](http://www.slf4j.org/)
  * [Logback](http://logback.qos.ch/)

For testing, the following libraries are used:

  * [FEST Swing](https://code.google.com/p/fest/)
  * [JUnit](http://junit.org/)

For releases, our fat jar is slimmed with [Proguard](http://proguard.sourceforge.net/).

Building the Code
-----------------

To build the code, run the Gradle wrapper. See the installation section.

Todo
----

See [here](TODO.md).

Limitations
-----------

OS X `WatchService` implementation is currently polling-based and extremely
slow. Support status can be found [here under "WatchService"](https://wiki.openjdk.java.net/display/MacOSXPort/Mac+OS+X+Port+Project+Status).

License
-------

This software is licensed under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).
