/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.OI;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.TeleOpCommand;

/**
 *
 * @author 128925
 */
public class DriveSystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public boolean upShiftCheck;
    public boolean downShiftCheck;
    public RobotDrive robotDriver = RobotMap.robotDrive;
    
    public void initDefaultCommand() {
        setDefaultCommand(new TeleOpCommand());
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void joystickDrive(double a,double b){
        robotDriver.arcadeDrive(a,b);
        
}
    
    public void upShift(){
        upShiftCheck = OI.shiftUp.get();
        if(upShiftCheck==true){
            RobotMap.upShifter.set(.5);
            Timer.delay(1);
            RobotMap.upShifter.set(0);
                  
        }
    }
        
    public void downShift(){
       downShiftCheck = OI.shiftDown.get();
       if(downShiftCheck==true){
           RobotMap.downShifter.set(.5);
           Timer.delay(1);
           RobotMap.downShifter.set(0);
       }
    }
    
    
    
}
