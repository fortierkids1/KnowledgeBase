Requires:
[[Motor Control]]

#### Completion Requirements
- ???


#### Synopsis
Feedforwards model an expected motor output for a  system to hit specific target values. 
The easiest example is a motor roller, which  you want to run at ~3000 RPM. You know your motor has a top speed of ~6000 RPM at 100% output, so you'd expect that 50% would get about 3000 RPM. As such, you can assume `motor.set(0.5)` is pretty close to your goal. 

With FeedForwards and a bit of knowledge about your system and motors, you can provide precise control without having to do extensive or challenging tuning of Feedback systems like [[PID]]s . The combination of PIDs and feedforwards can enable very accurate mechanisms with a minimal of trouble. 

