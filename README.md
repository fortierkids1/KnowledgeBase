Git-Testing
===========

Welcome StormBots!

I'm setting this up as a handy way to quickly test out git features in a safe environment, while we set things up. 

There's not much here quite yet, but feel free to check out the available resources. 


Learning Java
---
[WPLib Getting Started](http://wpilib.screenstepslive.com/s/3120/m/7885/l/79405-installing-the-java-development-tools) Walks you through programming your first robot anbd setting up neatbeans.

[Getting Started with Java for FRC](http://first.wpi.edu/Images/CMS/First/Getting_Started_with_Java_for_FRC.pdf)

[FRC Java Center](http://first.wpi.edu/FRC/frcjava.html)

[Mean Machines' GitHub](https://github.com/tmm2471) has some very cool example code, as well as cad models for some components.


Interfacing with Sensors
------------------------
Many of these are discussed with basic sample code in the [WPILib Users's Guide](http://first.wpi.edu/Images/CMS/First/WPILibUsersGuide.pdf). That's a very helpful starting point, and then additional documention is listed at their [primary documentation site](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85672-what-is-wpilib), some of which is specified below.

Distance Sensors/Sonar: See analog

[Quadrature Encoders](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85770-measuring-rotation-of-a-wheel-or-other-shaft-using-encoders) are useful for very precise monitoring of rotation and position. This is used on the BunnyBot. It's also very important for many PID controllers.

I2C

[Analog Sensors](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85775-analog-inputs) are handy for a variety of sensors, such as potentiometers and range sensors

[Analog Triggers](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85776-analog-triggers) help set up "switches" when measuring movements with analog sensors. 

Absolute Encoders

[Geartooth and Counter library](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85635-using-counters) is useful for monitoring shaft rotation and high-speed wheel rotation. 


Interfacing with motors and actuators
-------------------------------------
Ther'es lots of discussion here: [Speed Controller Objects](http://wpilib.screenstepslive.com/s/3120/m/7912/c/38335)

[PID Controllers](http://wpilib.screenstepslive.com/s/3120/m/7912/l/79828-operating-the-robot-with-feedback-from-sensors-pid-control)

[Servos](http://wpilib.screenstepslive.com/s/3120/m/7912/l/132341-repeatable-low-power-movement-controlling-servos-with-wpilib)


Other useful libraries and tools
--------------------------------
DriverStation

Network Communication

CAN Bus

Read Team color from the Control Center

State Machines

Threaded processing


Learning Git and Github
=======================

Don't let the complexity of all the commands fool you. A lot of what we'll be doing with Git is built in to the IDE, or the [Github for Windows](http://windows.github.com/) application. The important thing is to get the general feel for what the commands are called, and what they do.

Also of note, there's several different guides because they approach Git in different ways, some of which may jive with your brain better. Don't think that you need to fully understand all the commands, only the general workflow and tasks. Ask questions if need be!

[Github plugin for Eclipse](http://eclipse.github.com/)

[Github Plugin for NetBeans](https://netbeans.org/kb/73/ide/git.html)


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


Python Tutorials
=============================

Board Communication
----------------

[Twisted](https://twistedmatrix.com/trac/) Network protocol library for Python. There's a chance we may use this for communication, depending on simplicity.
We might still use I2C, assuming that the OS supports running it in slave mode. Not finding a lot of documentation at the moment. 

SimpleCV
--------
[Pycon Discussion](http://simplecv.org/news/2013/03/simplecv-talk-pycon) Long, excellent talk, gives a very good basis for what we'll be doing.

[SimpleCV tutorials](http://tutorial.simplecv.org/en/latest/) Enough said. 

While going through these, you'll hear "histogram" mentioned a lot. This [photography discussion](http://www.luminous-landscape.com/tutorials/understanding-series/understanding-histograms.shtml) has a very good explanation of what they are, and how to read them. After that, what the manipulations of them makes a lot more sense.

[Processing Vision Targets](http://wpilib.screenstepslive.com/s/3120/m/8731) is 2014 specific tutorial on how to process targets. There's some additional resources from their [Vision Processing](http://wpilib.screenstepslive.com/s/3120/m/8731) sections, but those are largely oriented towards implimenting vision control in Java or LabView.

