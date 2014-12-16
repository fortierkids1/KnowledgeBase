Best Practices and Conventions
==============================

This is a brief guide to help get accustomed to coding in a way that's meaningful to you and others. 



Naming Schemes
--------------
TODO: Class names, instance names, variable names, constants


When naming physical sections of the robot, ensure that the name is meaningful and obvious. 
Eg, for a motor that controls the left drive system, we want 
`MotorLeft` or `motorDriveLeft`, not `motor1` or `motor2`. In some cases (such as drive trains), 
we'll have multiple motors that control the same systems and always run at the same speed. In 
these cases adding a number is fine, as long as the rest is meaningful as well. Eg, `motorDriveLeft1`
 and `motorDriveLeft2`

On a similar note, parts that fit well with the `left` and `right` notation should always be named 
according to the *robot's* right. Just like when you're looking at someone their right arm is on
your left, it's still *their* right arm. If necessary, stand next to the robot, and face the direction
it would go if it were going forward. Now name it. 
This confusion is precisely what led mariners to use the notation "port" and "starboard". 


Code Restructuring
------------------
As a general rule, don't move code around unless you're making meaningful changes. Adding, subtracting, or 
changing lines that affect operation are fine.
Reformarmatting causes the algorithms Git uses to behave strangely when 
pulling in someone else's code. It's a quick and easy way to cause crashes and bugs when your 
Git branch goes back into the master branch. 

Inevitably the code _will_ need to be cleaned up (especially before post-season publishing), 
 but before you do, talk it over with the team and make sure it won't affect anyone else's branches. 

As an example of this, take the following code
```java
public static Motor fore(1,2);
public static Motor aft(1,4);
public static Motor port(1,5);
public static Motor starboard(1,1);
public static Motor sail(1,3);
```
Sorting it seems totally harmless, right? 
```java
public static Motor starboard(1,1);
public static Motor fore(1,2);
public static Motor sail(1,3);
public static Motor aft(1,4);
public static Motor port(1,5);
```
Well, not quite. Controls changed the port assignment, and someone corrected it with this
```java
public static Motor fore(1,2);
public static Motor aft(1,3); //changed
public static Motor port(1,5);
public static Motor starboard(1,1);
public static Motor sail(1,4); //changed
```
But, now you have a merge conflict, becasue Git recognizes `Fore`, as the top line, 
but now it thinks you added `Starboard` above it. Here's what attempting to merge these
changes results in:
```java
public static Motor starboard(1,1);
public static Motor fore(1,2);
<Merge Conflict>	//Thinks it's either Aft(1,3) or Sail(1,3)
public static Motor port(1,5);
<Merge Conflict>	//Not sure whether it's Sail(1,3) or Sail(1,4)
```
Now, someone has to hunt the both of you down, and have you figure out what's actually correct.
This is a lot of uneccessary labor, and can be avoided by communicating with your team in advance.







