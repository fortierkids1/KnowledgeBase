---
tags:
  - stub
aliases:
  - Indexer
  - Passthrough
  - Feeder
---
## Synopsis
Superstructure component that adds additional control axes between intakes and scoring mechanisms. In practice, indexers often temporarily act as part of those systems at different points in time, as well performing it's own specialized tasks.

 Common when handling multiple game pieces for storage and alignment, game pieces require re-orientation, adjustment or temporary storage, and for flywheel systems which need to isolate game piece motion from spinup.
 
## Success Criteria
- [ ] ???

## Code Considerations

Setting up an indexer is often a challenging process. It will naturally inherit several design goals and challenges from the systems it's connected to. This means it will often have a more complex API than most systems, often adopting notation from the connected systems. 

The Indexer is often sensitive to hardware design quirks and changes from those adjacent systems, which can change their behavior, and thus the interfacing code. 

Additionally, game piece handoffs can be mechanically complex, and imperfect. Often Indexers absorb special handling and fault detection, or at least bring such issues to light. Nominally, any such quirks are identified and hardware solutions implemented, or additional sensing is provided to facilitate code resolutions.

### Sensing
Indexers typically require some specific information about the system state, and tend to be a place where some sort of sensor ends up as a core operational component. The exact type and placement can vary by archtype, but often involve 
- Break beam sensors: These provide a non-contact, robust way to check game piece 
- Current/speed sensing: Many game pieces can be felt by the 

## Indexer Archtypes









