package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PWM;
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
    public static AnalogChannel potReader;
    public static PWM red;
    public static PWM green;
    public static PWM blue;
    public static DigitalInput lever1;
    public static DigitalInput lever2;
    public static DigitalInput lever3;
    public static int lever1state;
    public static int lever2state;
    public static int lever3state;
    
    public static void init() {
        leftMotor = new Victor(1,1);
        rightMotor = new Victor(1,4);
        upShifter = new Jaguar(1,2);
        downShifter = new Jaguar(1,3);
        robotDrive = new RobotDrive(leftMotor,rightMotor);
        potReader = new AnalogChannel(1);
        red = new PWM(10);
        green = new PWM(11);
        blue = new PWM(12);
        lever1 = new DigitalInput(1);
        lever2 = new DigitalInput(2);
        lever3 = new DigitalInput(3);
        
        
    }
    
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static final int rangefinderPort = 1;
    // public static final int rangefinderModule = 1;
}
