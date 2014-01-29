/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.RobotMap;

import edu.wpi.first.wpilibj.templates.commands.LightControl;

/**
 *
 * @author 128925
 */
public class Lights extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        setDefaultCommand(new LightControl());
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    public void intConvert(){
        if(RobotMap.lever1.get()==true){
            RobotMap.lever1state=0;
        }
        if(RobotMap.lever1.get()==false){
            RobotMap.lever1state=1;
        }
        if(RobotMap.lever2.get()==true){
            RobotMap.lever2state=0;
        }
        if(RobotMap.lever2.get()==false){
            RobotMap.lever2state=1;
        }
        if(RobotMap.lever3.get()==true){
            RobotMap.lever3state=0;
        }
        if(RobotMap.lever3.get()==false){
            RobotMap.lever3state=1;
        }
            
            
    }
}
