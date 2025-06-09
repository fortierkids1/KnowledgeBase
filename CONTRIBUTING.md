# Contributing

## Document Formatting Reference

Documents should generally be structured with the consideration that they're a small part of a whole; Each document is a little workshop, and intended to both provide some education, and provide a small task for the reader. 

Every document will likely be first encountered through the skill trees, so making sure that the first few lines are useful in the progression context will help those skill trees read better. 

> [!NOTE] Document Title - 
> A short synopsis or summary helping clarify the topic for at-a-glance reminders on the skill tree.
> 
> ## Success Criteria
> - [ ] Checkbox list of items that individually demonstrate a partial understanding of the document's goals
> - [ ] Items can be breadcrumbs of smaller steps toward the larger goal, in cases where reader background might not make it obvious otherwise
> - [ ] Items can also be generic requests to handle something a particular way, if the way to do so should be well understood
> - [ ] Items can also simply request variations
> 
> ## Goal
> A summary of what this the reader should understand after understanding the article's success criteria. This is not always required, but can be useful for more abstract or difficult topics.
> 
> ## Freeform topics
> Currently, no additional guidelines exist. Just add things



## What to document
The core details we want to document:
- What something is
- How it works
- Why we want to use it
- When to use it
- Where it goes in your code

When writing documentation, follow the graphs! You can expect that your reader has 
- Understood all documents leading up to this one
- Not read documents after this one. 
If you're trying to write documentation and find that you _cannot_ follow the graph, start a discussion about it. It may help to break up the topic at hand, or adjust the graph to better suit it. 

Good things to document
- Hard earned lessons, especially if they're non-obvious when understanding a topic. A good example is that feed-forwards make PIDs _much_ easier to work with, even if they're not _required_ for systems to work.   
- Custom idioms that we as a team like to use, or alterations to the existing documentation. 
- Documenting the "why" is often forgotten, but very valuable. 


Documentation considerations: 
- If there's good existing documentation, consider just linking it and building on top of it! This is especially true of concepts at [docs.wpilib.org]. This means we can provide guidance and workflow, without having additional upkeep on our own documentation. 
- However, consider offline use and reader flow. If something is quick and easy to explain, and unlikely to change season to season, it's may be worth adding just to make things easy. 
- Avoid duplicating information in multiple documents here. If we explain it elsewhere, just link to it. This prevents conflicting information and excessive work maintaining the notebook. 
- 



## Useful formatting features

#### Standard features

Some of the basic markdown features are critical to being effective in adding and extending the notebook:
https://help.obsidian.md/syntax

On occasion, we'll need other stuff from here:  https://help.obsidian.md/advanced-syntax

#### Embeds
We can embed pictures! Drag and drop or copy and paste is probably the easiest way.
When doing so, it'll default to storing the file in the project root directly. In general, just shove pictures in the `assets/` folder so they're out of the way. Renaming them to be vaguely descriptive is recommended though. 



#### Callouts 
A callout is a useful structure for little inline sidebars with various appearances
https://help.obsidian.md/callouts

> [!info] 
> This is what a callout looks like

> [!Caution]
> Use caution and warn callouts when something is easy to get wrong and result in surprises. 
> If something going wrong provides a safety risk, consider the danger tag.

> [!bug]
> Bug-type callout. Useful for indicating when something is actively broken, or works unexpectedly

#### Tables and calculations


Basic table format is 
```markdown
|col name 1| colname 2| etc |
|----------|----------|-----|
| row one  | stuff    |     |
```
But you'll probably just want to use `CTRL+P` and select Insert Table

This use a plugin to enable excel-like math operations should it prove useful for helping in explaining concepts.
https://github.com/klaudyu/CalcCraft

| t              | sin(t)       | Note:                                 |
| -------------- | ------------ | ------------------------------------- |
| 0              | =sin(a2)     |                                       |
| =PI/4          | =sin(a3)     | Can use variables and the like        |
| =(-0c-1r)+PI/4 | =sin(-1c-0r) | Using the relative reference notation |
| =(-0c-1r)+PI/4 | =sin(-1c-0r) |                                       |
| =(-0c-1r)+PI/4 | =sin(-1c-0r) |                                       |
note: these are only evaluated in "reader" mode (click the book icon on the top right of this page window)

#### State Diagrams
Beyond the Canvas utilities, you can embed a variety of diagrams generated by Mermaid. This utility can assist in learning and configuring diagrams efficiently.
https://mermaid.live/edit

#### Assistance
If you forget how to set up some syntax detail, just select some text, and hit CTRL+P to open the command bar. Enter whatever it is you're after, and obsidian will help you out. 

When all else fails, just write it! We can always tidy up later. 
