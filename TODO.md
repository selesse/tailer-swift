Todo
====

* Be able to watch files that do not exist yet. May be complicated since that
  directory has to exist first. Also may be complicated because the file
  chooser might expect a file that exists.
* UI indications when watched files are deleted, are created, another open tab
  has been modified/deleted.
* Display preferences: line spacing, line wrapping.
* Merge foreground/background color picker into 1, with live preview.
* Create cut "feature". Functionality equivalent of $(cut -f1 -d'|' $file).
* Add global tabs for every "feature".
* Don't overwrite settings for multiple instances?
* Tests. (work in progress)

Bugs
====
* Line number makes it seem like there's an n+1th line when there isn't. In
  other words, if your file is 10 lines long, it'll show 11 lines long.
* Fix the "feature" buttons (press Search, then Filter, then Highlight).
* Mac: Figure out why shortcuts aren't being displayed in menus.
* Fix smart scrolling when loading a long/large file.

Done
====
* Add smart scrolling (Oct 31 2013).
* Make sure threads are being exited (Oct 31 2013).
* Add font / display customizations (Nov 3 2013).
* Add (basic) highlighting tab (Nov 3 2013).
* Add (basic) highlighting functionality (Nov 3 2013).
* Add (basic) searching tab (Nov 3 2013).
* Add (basic) searching functionality (Nov 3 2013).
* Remember last focused tab, reinitialize it on startup (Nov 3 2013).
* (Fixed) Fix line endings. Maybe read everything as "\n" to avoid "\r\n" vs
  "\n" highlighting problem (Nov 4 2013).
* Use default font in Font Chooser (Nov 4 2013).
* Add line numbers (Nov 4 2013).
* (Fixed) If you highlight a word, append it to an already-open file, it
  doesn't get highlighted (Nov 4 2013).
* Smoothen GUI initialization (Nov 4 2013).
* Our jars are now on a diet. Thanks, Proguard! (in other words: Proguard
  support) (Nov 9 2013).
* Use logback + slf4j. Might only use this for debugging (Nov 9 2013).
* Add FEST, and our first unit test. This took way too long, sorry! (Nov 9
  2013)
* Make "release" depend on tests and Proguard. This means that whenever we do
  a release, our jar will be skinny and pass all the tests. (Nov 10 2013)
