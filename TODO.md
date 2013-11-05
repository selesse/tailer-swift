Todo
====

* Be able to watch files that do not exist yet. May be complicated since that
  directory has to exist first. Also may be complicated because the file
  chooser might expect a file that exists.
* UI indications when watched files are deleted, are created, another open tab
  has been modified/deleted.
* Smoothen GUI initialization.
* Mac: Figure out why shortcuts aren't being displayed in menus.

Bugs
====
* If you highlight a word, append it to an already-open file, it doesn't
  get highlighted.
* Line number makes it seem like there's an n+1th line when there isn't. In
  other words, if your file is 10 lines long, it'll show 11 lines long.

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
* Fix line endings. Maybe read everything as "\n" to avoid "\r\n" vs "\n"
  highlighting problem (Nov 4 2013).
* Use default font in Font Chooser (Nov 4 2013).
* Add line numbers (Nov 4 2013).
