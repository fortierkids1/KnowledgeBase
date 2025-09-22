---
aliases:
  - Dimensional Analysis
---

Factor Label is an analytic of using units to help guide conversion between different unit types. 

#### Walkthrough: Days to Seconds
First, just start with your initial units. In this example, let's start with 1 day, and try to get to seconds. 

| -      |     |     |     |     | -    |
| ------ | --- | --- | --- | --- | ---- |
| 1 days |     |     |     | ->  | days |
|        |     |     |     |     |      |

We multiply by 24 hours per day:

| -          | -         | -   | -   |     | -     |
| ---------- | --------- | --- | --- | --- | ----- |
| 1 ~~days~~ | 24 hours  |     |     | ->  | hours |
|            | 1 ~~day~~ |     |     |     |       |
Here we cancel out the unit "day", so we cancel it out, leaving us with a unit of Hours. But that's not seconds, so let's keep going.

| -          | -            | -          | -   |     | -       |
| ---------- | ------------ | ---------- | --- | --- | ------- |
| 1 ~~days~~ | 24 ~~hours~~ | 60 minutes |     | ->  | seconds |
|            | 1 ~~day~~    | 1 ~~hour~~ |     |     |         |

| -          | -            | -              | -            |     | -       |
| ---------- | ------------ | -------------- | ------------ | --- | ------- |
| 1 ~~days~~ | 24 ~~hours~~ | 60 ~~minutes~~ | 60 seconds   | ->  | seconds |
|            | 1 ~~day~~    | 1 ~~hour~~     | 1 ~~minute~~ |     |         |

Awesome! We've now cancelled out all our units except the intended ones, and we just have a lot of numbers left over. Let's actually crunch the numbers and figure this out:
$$1 *(24/1)*(60/1)*(60/1) seconds = 86400 seconds$$
a helpful property comes from doing PEMDAS too: You can just multiply out the top, and divide out by the bottom. This is not helpful for code (you lose the relationships), but saves a lot of hassle if you're plugging things into calculators!
$$1*24*60*60/1/1/1 = 86400$$

#### Practical Application

We'll have a *lot* of conversions and transformations between units in our code! This includes motor rotation -> other real world units, converting between unit types or proportions. If there's a conversion between A->B then this can be helpful. Arbitrary units like "input rotation" and "output rotation" are common ones, as is things like Gear A->Gear B.

The important part of Factor Label is that by being able to step through the dimensions, you can easily catch several common errors, such as multiplying instead of dividing, or having a "1/unit" error. 

#### Example: KOP DriveTrain velocity

| motor rev        | time       | gear                  | wheel             |     | output  |
| ---------------- | ---------- | --------------------- | ----------------- | --- | ------- |
| 1 motor rotation | 1 min      | 1 output rotation     | Pi * 4 inches     | ->  | ?? inch |
| minute           | 60 seconds | 12.75 motor rotations | 1 output rotation |     | s       |

^a78bfd


#### Example: Catching a conversion error

Let's assume we tried the above computation, and got the gearing and wheel conversions backwards.  

| motor rev        | time       | gear                  | wheel             |     | output |
| ---------------- | ---------- | --------------------- | ----------------- | --- | ------ |
| 1 motor rotation | 1 minute   | 12.75 motor rotations | 1 output rotation | ->  | ??     |
| minute           | 60 seconds | 1 output rotation     | Pi * 4 inches     |     |        |
If we do our unit analysis,,,,

| motor rev        | time         | gear                  | wheel                 |     | output           |
| ---------------- | ------------ | --------------------- | --------------------- | --- | ---------------- |
| 1 motor rotation | 1 ~~minute~~ | 12.75 motor rotations | 1 ~~output rotation~~ | ->  | motor rotation^2 |
| ~~minute~~       | 60 seconds   | 1 ~~output rotation~~ | Pi * 4 inches         |     | seconds*inches   |

We can clearly see it's not right. Bug caught!
