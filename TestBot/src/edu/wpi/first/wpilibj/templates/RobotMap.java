package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // For example to map the left and right motors, you could define the
    // following variables to use with your drivetrain subsystem.
    public static SpeedController leftMotor;
    public static SpeedController rightMotor;
    public static SpeedController upShifter;
    public static SpeedController downShifter;
    public static RobotDrive robotDrive;
    
    public static void init() {
        leftMotor = new Victor(1,1);
        rightMotor = new Victor(1,4);
        upShifter = new Jaguar(1,2);
        downShifter = new Jaguar(1,3);
        robotDrive = new RobotDrive(leftMotor,rightMotor);
        
    }
    
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static final int rangefinderPort = 1;
    // public static final int rangefinderModule = 1;
}
