/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.SensorInput;

/**
 *
 * @author 128925
 */
public class PotSensor extends Subsystem {
    public int potOutput;
    public AnalogChannel potReader = RobotMap.potReader;
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        setDefaultCommand(new SensorInput());
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void potCheck(boolean p){
        potOutput = potReader.getAverageValue();
        if(p == true){
            System.out.println(potOutput);
        }
    }
}
