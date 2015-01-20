Learning Git and Github
=======================

Don't let the complexity of all the commands fool you. A lot of what we'll be doing with Git is built in to the IDE, or the [Github for Windows](http://windows.github.com/) application. The important thing is to get the general feel for what the commands are called, and what they do.

Also of note, there's several different guides because they approach Git in different ways, some of which may jive with your brain better. Don't think that you need to fully understand all the commands, only the general workflow and tasks. Ask questions if need be!

[Github plugin for Eclipse](http://eclipse.github.com/)

[Github Plugin for NetBeans](https://netbeans.org/kb/73/ide/git.html)

Once you start using Github, you'll eventually hit a merge conflict, which is difficult to solve without a visual tool. [KDiff3](http://sourceforge.net/projects/kdiff3/files/kdiff3/0.9.97/) is a very good tool for this, and can be configured to open automatically whenever a problem arises. To do this, open your .gitconfig file and add something like the following:
```INI
[merge]
    tool = kdiff3

[mergetool "kdiff3"]
    path = C:/YourPathToBinaryHere/KDiff3/kdiff3.exe
    keepBackup = false
    trustExitCode = false
```


Basics
------
[Using GitHub](https://learn.sparkfun.com/tutorials/using-github/) Very good general overview, particularly for using the GitHub for Windows application. 

[Git: The simple guide](http://rogerdudler.github.io/git-guide/) This one does a great job of describing the various tasks in Git.

[Git in 5 minutes](http://classic.scottr.org/presentations/git-in-5-minutes/)

[Pushing and Pulling](http://gitready.com/beginner/2009/01/21/pushing-and-pulling.html) Has a very nice visual reference for what's happening with the commands.


More advanced git stuff
-----------------------

[Getting Started with Git](http://git-scm.com/book/en/Getting-Started), which is much more comprehensive, and a pretty good resource for advanced use. Kelson, you'll want to read this.

[Interactive Git Cheatsheet](http://ndpsoftware.com/git-cheatsheet.html). Very cool for figuring out what goes from where to where. 

