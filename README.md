tailer-swift
============

tailer-swift is a cross-platform GUI specialization of the popular tail
utility. Highlights include easy highlighting, filtering and
regular-expression support.

Installation
------------

1. Clone this repository.
2. Type `gradle`.

Requirements
------------

[Gradle](http://gradle.org) is required to build the code.

  * [Guava](https://code.google.com/p/guava-libraries/)

Building the Code
-----------------

To build the code, run `gradle`.

Limitations
-----------

OS X `WatchService` implementation is currently polling-based and extremely
slow. Support status can be found [here under "WatchService"](https://wikis.oracle.com/display/OpenJDK/Mac+OS+X+Port+Project+Status).

License
-------

This software is licensed under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).
