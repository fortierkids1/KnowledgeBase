---
aliases:
  - Optional
---


Optionals are used to represent data that may or may not exist, and does so in a consistent, type safe way. This is often a convenient way to package data from sensors without extra functions involved. 

Let's look at a simple sensor example

```java
    VisionSystem vision = new Vision();
    var distance = vision.getDistanceToTarget();
    if(distance<5){
	    //Do something
    }else{
	    //Do something else
    }
```

The problem is that what if a target _does not_ exist? What happens when you call `vision.getDistanceToTarget()` anyway? What should that function return when data is invalid?

One option, common in java, is to return `null`. Unfortunately, `null` is not part of the type system, and Java will not _tell you_ when something could be null, and when performing basically any object on a null... your code crashes. As a result, we _never_ return a null from our functions. 

So, now we have to figure out what `getDistanceToTarget()` should return when called on invalid data. Should it return -1? 0? Arbitrarily large value? The last valid value? What if there was no prior valid value? There's no really good answer; Any of these return schemes require special handling, and likely will cause bugs somewhere

Optionals provide the answer here. This is an explicit, type controlled way to say "Hey, this data may not be valid", and provides helper functions to deal with it. 

```java
public VisionSystem(){
    public Optional<double> getDistanceToTarget(){
        if(/*target exists*/) return Optional.of(1.4);
        else( Optional.empty());
    }
}
```

```java
    VisionSystem vision = new Vision;
	
	//Var is an Optional that may contain a double
    var target = vision.getDistanceToTarget();
    //First, check to see if the optional contains data
    if(target.isPresent()){
	    //And, we can read it!
        target.get() //do something with the contained value
    }
    else{
        //do something else
    }
```

Cool! We're using optionals. While we solved the above issues, so far, we haven't gained anything in ease or convenience. Rather, we've just used the type system to prevent improper, unchecked code, and communicate this ambiguity. 


But now we can deploy helper functions to simplify things based on our needs. 

```java
target.isPresent() //true if we have a target; Maybe we don't even care about the distance. 
target.isEmpty() // true if no target

double distance = vision.getDistanceToTarget().orElseGet(0) //Assume zero if no target ; No need to explicitly check if a value exists
double distance = vision.getDistanceToTarget().orElseGet(99999) //Assume big value if no target; Again, no check needed.
```

These extra helper functions let us assign our own defaults based on our expectations, without having to complicate the library or API.

